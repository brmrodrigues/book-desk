(ns book-desk.db
  (:require [datomic.client.api :as d]))

(def schema [;User
             {:db/ident :user/id
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "User ID for this app"}
             {:db/ident :user/first-name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "User first name"}
             {:db/ident :user/last-name
              :db/valueType :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc "User last name"}
             {:db/ident :user/admin?
              :db/valueType :db.type/boolean
              :db/cardinality :db.cardinality/one
              :db/doc "Is user an admin of the app?"}

             ;Floor
             {:db/ident :floor/number
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/unique :db.unique/identity
              :db/doc "Floor number. E.g. 3rd floor"}
             {:db/ident :floor/size
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc "Size used to display desks for UI"}

             ;Booking
             {:db/ident :booking/user
              :db/valueType :db.type/uuid
              :db/cardinality :db.cardinality/one
              :db/doc "User UUID related to the booked desk"}
             {:db/ident :booking/floor
              :db/valueType :db.type/long
              :db/cardinality :db.cardinality/one
              :db/doc "Floor related to the booking"}])

(defn users [db]
  (d/q '[:find ?user-id
         :where
         [?u :user/id ?user-id]]
       db))

(defn floors [db]
  (d/q '[:find ?floor-number
         :where
         [?f :floor/number ?floor-number]]
       db))

(defn bookings [db]
  (d/q '[:find ?booking-floor ?booking-user
         :where
         [?b :booking/user ?booking-user]
         [?b :booking/floor ?booking-floor]]
       db))

(defn booking-tx
  [user-id
   floor-number]
  (-> [{:booking/floor floor-number
        :booking/user user-id}]))
        