(ns user
  (:require [book-desk.core :as core]
            [io.pedestal.http :as pedestal-http]
            [reitit.pedestal :as reitit-pedestal]
            [reitit.http :as reitit-http]
            [reitit.ring.middleware.dev :as dev]))

(defonce server (atom nil))

(def dev-server-config
 (->
  {:env :dev
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
  (reitit-pedestal/replace-last-interceptor core/router)
  (pedestal-http/dev-interceptors)))

(defn start! []
  (when (nil? @server)
    (reset! server (core/start-server dev-server-config))
    (println "server started in port 3000")))

(defn stop! []
  (when @server
    (pedestal-http/stop @server)
    (reset! server nil)
    (println "server stopped")))

(comment
  (start!)
  (stop!))
