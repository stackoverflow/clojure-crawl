(ns clojure-crawl.items
  (:use clojure-crawl.things
	clojure-crawl.utils)
  (:import (clojure-crawl.things Item AFix)))

(def *prefixes*
     [{:name "Jagged", :level 10, :type [:weapon], :effect {:attack (range 1 6)}}
      {:name "Deadly", :level 20, :type [:weapon], :effect {:attack (range 9 14)}}
      {:name "Heavy", :level 30, :type [:weapon], :effect {:attack (range 17 22)}}
      {:name "Brutal", :level 40, :type [:weapon], :effect {:attack (range 25 30)}}
      {:name "Massive", :level 50, :type [:weapon], :effect {:attack (range 33 38)}}
      {:name "Savage", :level 60, :type [:weapon], :effect {:attack (range 41 46)}}
      {:name "Ruthless", :level 70, :type [:weapon], :effect {:attack (range 49 54)}}
      {:name "Merciless", :level 80, :type [:weapon], :effect {:attack (range 57 62)}}
      {:name "Sturdy", :level 10, :type [:armor :shield], :effect {:defense (range 1 4)}}
      {:name "Strong", :level 20, :type [:armor :shield], :effect {:defense (range 5 8)}}
      {:name "Glorious", :level 30, :type [:armor :shield], :effect {:defense (range 9 12)}}
      {:name "Blessed", :level 40, :type [:armor :shield], :effect {:defense (range 13 16)}}
      {:name "Faithfull", :level 50, :type [:armor :shield], :effect {:defense (range 17 20)}}
      {:name "Saintly", :level 60, :type [:armor :shield], :effect {:defense (range 21 24)}}
      {:name "Holy", :level 70, :type [:armor :shield], :effect {:defense (range 25 28)}}
      {:name "Godly", :level 80, :type [:armor :shield], :effect {:defense (range 29 32)}}
      {:name "Lizard", :level 10, :type [:armor :shield :ring], :effect {:mana (range 1 6)}}
      {:name "Snake", :level 20, :type [:armor :shield :ring], :effect {:mana (range 9 14)}}
      {:name "Serpent", :level 30, :type [:armor :shield :ring], :effect {:mana (range 17 22)}}
      {:name "Drake", :level 40, :type [:armor :shield :ring], :effect {:mana (range 25 30)}}
      {:name "Dragon", :level 50, :type [:armor :shield :ring], :effect {:mana (range 33 38)}}
      {:name "Wyrm", :level 60, :type [:armor :shield :ring], :effect {:mana (range 41 46)}}
      {:name "Great Wyrm", :level 70, :type [:armor :shield :ring], :effect {:mana (range 49 54)}}
      {:name "Bahamut", :level 80, :type [:armor :shield :ring], :effect {:mana (range 57 62)}}
      {:name "Critical", :level 10, :type [:weapon :ring], :effect {:critical (range 1 3)}}
      {:name "Precise", :level 30, :type [:weapon :ring], :effect {:critical (range 3 5)}}
      {:name "Unavoidable", :level 50, :type [:weapon :ring], :effect {:critical (range 5 7)}}
      ])

(def *suffixes*
     [{:name "Strength", :level 10, :type [:any], :effect {:strength (range 1 4)}}
      {:name "Might", :level 20, :type [:any], :effect {:strength (range 5 8)}}
      {:name "the Ox", :level 30, :type [:any], :effect {:strength (range 9 12)}}
      {:name "the Brute", :level 40, :type [:any], :effect {:strength (range 13 16)}}
      {:name "Power", :level 50, :type [:any], :effect {:strength (range 17 20)}}
      {:name "the Giant", :level 60, :type [:any], :effect {:strength (range 21 24)}}
      {:name "the Titan", :level 70, :type [:any], :effect {:strength (range 25 28)}}
      {:name "Atlas", :level 80, :type [:any], :effect {:strength (range 29 32)}}
      {:name "Dexterity", :level 10, :type [:any], :effect {:agility (range 1 4)}}
      {:name "Agility", :level 20, :type [:any], :effect {:agility (range 5 8)}}
      {:name "Skill", :level 30, :type [:any], :effect {:agility (range 9 12)}}
      {:name "Accuracy", :level 40, :type [:any], :effect {:agility (range 13 16)}}
      {:name "Precision", :level 50, :type [:any], :effect {:agility (range 17 20)}}
      {:name "the Thief", :level 60, :type [:any], :effect {:agility (range 21 24)}}
      {:name "Perfection", :level 70, :type [:any], :effect {:agility (range 25 28)}}
      {:name "Nirvana", :level 80, :type [:any], :effect {:agility (range 29 32)}}
      {:name "the Apprentice", :level 10, :type [:any], :effect {:magic (range 1 4)}}
      {:name "the Novice", :level 20, :type [:any], :effect {:magic (range 5 8)}}
      {:name "Magic", :level 30, :type [:any], :effect {:magic (range 9 12)}}
      {:name "the Witch", :level 40, :type [:any], :effect {:magic (range 13 16)}}
      {:name "the Mage", :level 50, :type [:any], :effect {:magic (range 17 20)}}
      {:name "the Wizard", :level 60, :type [:any], :effect {:magic (range 21 24)}}
      {:name "the Sorcerer", :level 70, :type [:any], :effect {:magic (range 25 28)}}
      {:name "the Archimage", :level 80, :type [:any], :effect {:magic (range 29 32)}}
      {:name "the Jackal", :level 10, :type [:armor :shield :ring], :effect {:life (range 1 10)}}
      {:name "the Fox", :level 20, :type [:armor :shield :ring], :effect {:life (range 25 40)}}
      {:name "the Wolf", :level 30, :type [:armor :shield :ring], :effect {:life (range 50 70)}}
      {:name "the Tiger", :level 40, :type [:armor :shield :ring], :effect {:life (range 70 100)}}
      {:name "the Mammoth", :level 50, :type [:armor :shield :ring], :effect {:life (range 100 150)}}
      {:name "the Colossus", :level 60, :type [:armor :shield :ring], :effect {:life (range 150 200)}}
      {:name "the Squid", :level 70, :type [:armor :shield :ring], :effect {:life (range 200 250)}}
      {:name "the Whale", :level 80, :type [:armor :shield :ring], :effect {:life (range 250 300)}}
      {:name "Evade", :level 10, :type [:shield :ring], :effect {:evade (range 1 3)}}
      {:name "Dodge", :level 30, :type [:shield :ring], :effect {:evade (range 3 5)}}
      {:name "Invisibility", :level 50, :type [:shield :ring], :effect {:evade (range 5 7)}}
      ])

(def *equips*
     [(new Item "Wooden Sword" "A sword made of wood" :weapon :sword nil nil 1
	   (create-effect {:attack 2}))])

(defn- gen-random-map [[k v]]
  (if (seq? v)
    [k (rand-nth v)]
    [k v]))

(defn- get-fix [name fix]
  (let [coll (if (= fix :prefix) *prefixes* *suffixes*)
	fx (first (filter #(= name (:name %)) coll))
	eff (create-effect (into {} (map gen-random-map (:effect fx))))]
    (new AFix name fix (:level fx) eff)))

(defn- get-prefix [name]
  (get-fix name :prefix))

(defn- get-suffix [name]
  (get-fix name :suffix))

(defn- get-random-prefix [level]
  (let [r (fn [m] (some #{(:level m)} (range (- level 20) (+ level 10))))
	name (rand-nth (filter r *prefixes*))]
    (get-prefix name)))

(defn- get-random-suffix [level]
  (let [name (rand-nth (filter #(between-range (:level %) level 5) *suffixes*))]
    (get-suffix name)))