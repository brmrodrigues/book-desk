(ns book-desk.fixture
  (:import java.util.UUID))

(defn new-users []
  [{:user/id (UUID/randomUUID)
    :user/first-name "Bruno"
    :user/last-name "Rodrigues"
    :user/admin? true}
   {:user/id (UUID/randomUUID)
    :user/first-name "Renata"
    :user/last-name "Reis"
    :user/admin? false}])

(def floors
  [{:floor/number 3
    :floor/size 250}])
