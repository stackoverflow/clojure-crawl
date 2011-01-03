(ns clojure-crawl.utils
  (:import (java.text DecimalFormat))
  (:require [clojure [string :as string]]))

(defn rand-between [start end]
  (let [diff (- end start)
	r (rand diff)]
    (+ r start)))

(defn add-to! [atom & items]
  (apply swap! atom conj items))

(defn eval-string [s]
  (eval (read-string s)))

(defn to-num [n]
  (let [f (new DecimalFormat "0.##")]
    (. (. f format (double n)) (replaceAll "," "\\."))))

(defn pos-num [n]
  (if (< n 0)
    0
    (int n)))

(defn vec->damage [v]
  (str (to-num (first v)) " - " (to-num (second v))))

(defn attack->str [att]
  (str (when (:critical att)
	 "Critical Hit! ")
       (if (:evade att)
	 "Enemy Evaded "
	 (str "Caused " (int (:damage att)) " damage. "))
       (when (:exp att)
	 (str "Gained " (:exp att) " experience. "))
       (when (:level-up att)
	 "LEVEL UP!")))

(defn apply-in-vec [f v]
  (vec (map f v)))

(defn key->name [key]
  (let [name (string/capitalize (subs (str key) 1))]
    (if (. name contains "-")
      (. name replaceAll "-" " ")
      name)))

(defn name->key [name]
  (keyword (. name toLowerCase)))

(defn probability-result [n]
  (> n (inc (rand-int 100))))