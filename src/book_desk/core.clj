(ns book-desk.core
  (:gen-class)
  (:require [io.pedestal.http :as pedestal-http]
            [reitit.ring :as ring]
            [reitit.http :as reitit-http]
            [reitit.coercion.malli]
            [reitit.ring.malli]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.http.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.http.interceptors.parameters :as parameters]
            [reitit.http.interceptors.muuntaja :as muuntaja]
            [reitit.http.interceptors.exception :as exception]
            [reitit.pedestal :as reitit-pedestal]
            [clojure.core.async :as a]
            [clojure.java.io :as io]
            [muuntaja.core :as m]
            [malli.util :as mu]))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "Book a Desk API"
                            :description "Book a desk safely ;)"}}
           :handler (swagger/create-swagger-handler)}}]

   ["/math"
    {:swagger {:tags ["math"]}}

    ["/plus"
     {:get {:summary "plus with malli query parameters"
            :parameters {:query [:map [:x int?] [:y int?]]}
            :responses {200 {:body [:map [:total int?]]}}
            :handler (fn [{{{:keys [x y]} :query} :parameters}]
                       {:status 200
                        :body {:total (+ x y)}})}
      :post {:summary "plus with malli body parameters"
             :parameters {:body [:map [:x int?] [:y int?]]}
             :responses {200 {:body [:map [:total int?]]}}
             :handler (fn [{{{:keys [x y]} :body} :parameters}]
                        {:status 200
                         :body {:total (+ x y)}})}}]]])

(def router-opts
  {:exception pretty/exception
   :data {:coercion (reitit.coercion.malli/create
                      {;; set of keys to include in error messages
                       :error-keys #{:type :coercion :in :schema :value :errors :humanized :transformed}
                       ;; schema identity function (default: close all map schemas)
                       :compile mu/closed-schema
                       ;; strip-extra-keys (effects only predefined transformers)
                       :strip-extra-keys true
                       ;; add/set default values
                       :default-values true
                       ;; malli options
                       :options nil})
          :muuntaja m/instance
          :interceptors [;; swagger feature
                         swagger/swagger-feature
                         ;; query-params & form-params
                         (parameters/parameters-interceptor)
                         ;; content-negotiation
                         (muuntaja/format-negotiate-interceptor)
                         ;; encoding response body
                         (muuntaja/format-response-interceptor)
                         ;; exception handling
                         (exception/exception-interceptor)
                         ;; decoding request body
                         (muuntaja/format-request-interceptor)
                         ;; coercing response bodys
                         (coercion/coerce-response-interceptor)
                         ;; coercing request parameters
                         (coercion/coerce-request-interceptor)]}})

(def router
  (reitit-pedestal/routing-interceptor
    (reitit-http/router routes router-opts)
    ;; optional default ring handler (if no routes have matched)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :operationsSorter "alpha"}})
      (ring/create-resource-handler)
      (ring/create-default-handler))))

(def server-config
  (->
   {:env :prod
    ::pedestal-http/type :jetty
    ::pedestal-http/port 3000
    ::pedestal-http/join? false
    ;; no pedestal routes
    ::pedestal-http/routes []
    ;; allow serving the swagger-ui styles & scripts from self
    ::pedestal-http/secure-headers {:content-security-policy-settings
                                    {:default-src "'self'"
                                     :style-src "'self' 'unsafe-inline'"
                                     :script-src "'self' 'unsafe-inline'"}}}
   (pedestal-http/default-interceptors)
   ;; use the reitit router
   (reitit-pedestal/replace-last-interceptor router)))

(defn start-server [server-config]
  (-> server-config
      (pedestal-http/create-server)
      (pedestal-http/start)))

(defn -main
  [& args]
  (start-server server-config))
