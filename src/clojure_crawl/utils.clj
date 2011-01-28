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
  (let [skill (:skill att)
	skill-up (:skill-level-up att)
	dmg (int (:damage att))]
    (str (when skill
	   (str (:name skill) " - "))
	 (when (:critical att)
	   "Critical Hit! ")
	 (if (:evade att)
	   "Enemy Evaded "
	   (if (>= dmg 0)
	     (str "Caused " dmg " damage. ")
	     (str "Healed " (- dmg) " life. ")))
	 (when (:exp att)
	   (str "Gained " (:exp att) " experience. "))
	 (when (:level-up att)
	   "LEVEL UP!")
	 (when skill-up
	   (str "Skill " (:name skill) " is now on level " @(:level skill) "! ")))))

(defn apply-in-vec [f v]
  (vec (map f v)))

(defn key->name [key]
  (if (not key)
    "-"
    (let [name (string/capitalize (subs (str key) 1))]
      (if (. name contains "-")
	(. name replaceAll "-" " ")
	name))))

(defn name->key [name]
  (keyword (. name toLowerCase)))

(defn probability-result [n]
  (> n (inc (rand-int 100))))

(defn between-range
  "Returns true if val is a number between center - distance and center + distance."
  [val center distance]
  (some #{val} (range (- center distance) (+ center distance))))

(defn super- [& vs]
  (let [fi (first vs)]
    (cond (instance? Number fi)
	  (apply - vs)
	  (vector? fi)
	  (vec (apply map - vs)))))