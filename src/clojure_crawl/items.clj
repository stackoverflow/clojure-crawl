(ns clojure-crawl.items
  (:use clojure-crawl.things
	clojure-crawl.utils)
  (:import (clojure-crawl.things Item AFix)))

(def *prefixes*
     [{:name "Jagged", :level 10, :type [:weapon], :effect {:attack (range 1 7)}}
      {:name "Deadly", :level 20, :type [:weapon], :effect {:attack (range 9 15)}}
      {:name "Heavy", :level 30, :type [:weapon], :effect {:attack (range 17 23)}}
      {:name "Brutal", :level 40, :type [:weapon], :effect {:attack (range 25 31)}}
      {:name "Massive", :level 50, :type [:weapon], :effect {:attack (range 33 39)}}
      {:name "Savage", :level 60, :type [:weapon], :effect {:attack (range 41 47)}}
      {:name "Ruthless", :level 70, :type [:weapon], :effect {:attack (range 49 55)}}
      {:name "Merciless", :level 80, :type [:weapon], :effect {:attack (range 57 63)}}
      {:name "Sturdy", :level 10, :type [:armor :shield], :effect {:defense (range 1 5)}}
      {:name "Strong", :level 20, :type [:armor :shield], :effect {:defense (range 5 9)}}
      {:name "Glorious", :level 30, :type [:armor :shield], :effect {:defense (range 9 13)}}
      {:name "Blessed", :level 40, :type [:armor :shield], :effect {:defense (range 13 17)}}
      {:name "Faithfull", :level 50, :type [:armor :shield], :effect {:defense (range 17 21)}}
      {:name "Saintly", :level 60, :type [:armor :shield], :effect {:defense (range 21 25)}}
      {:name "Holy", :level 70, :type [:armor :shield], :effect {:defense (range 25 29)}}
      {:name "Godly", :level 80, :type [:armor :shield], :effect {:defense (range 29 33)}}
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
      {:name "Bat", :level 10, :type [:armor :shield :ring], :effect {:mana-regen (range 20 61 20)}}
      {:name "Wraith", :level 30, :type [:armor :shield :ring], :effect {:mana-regen (range 80 121 20)}}
      {:name "Vampire", :level 50, :type [:armor :shield :ring], :effect {:mana-regen (range 140 181 20)}}
      {:name "Hidden", :level 10, :type [:armor :shield :ring], :effect {:hide (range 1 5)}}
      {:name "Secret", :level 20, :type [:armor :shield :ring], :effect {:hide (range 5 9)}}
      {:name "Occult", :level 30, :type [:armor :shield :ring], :effect {:hide (range 9 13)}}
      {:name "Shadow", :level 40, :type [:armor :shield :ring], :effect {:hide (range 13 17)}}
      {:name "Undetectable", :level 50, :type [:armor :shield :ring], :effect {:hide (range 17 21)}}
      {:name "Translucid", :level 60, :type [:armor :shield :ring], :effect {:hide (range 21 25)}}
      {:name "Mirror", :level 70, :type [:armor :shield :ring], :effect {:hide (range 25 29)}}
      {:name "Invisible", :level 80, :type [:armor :shield :ring], :effect {:hide (range 29 33)}}])

(def *suffixes*
     [{:name "Strength", :level 10, :type [:any], :effect {:strength (range 1 5)}}
      {:name "Might", :level 20, :type [:any], :effect {:strength (range 5 9)}}
      {:name "the Ox", :level 30, :type [:any], :effect {:strength (range 9 13)}}
      {:name "the Brute", :level 40, :type [:any], :effect {:strength (range 13 17)}}
      {:name "Power", :level 50, :type [:any], :effect {:strength (range 17 21)}}
      {:name "the Giant", :level 60, :type [:any], :effect {:strength (range 21 25)}}
      {:name "the Titan", :level 70, :type [:any], :effect {:strength (range 25 29)}}
      {:name "Atlas", :level 80, :type [:any], :effect {:strength (range 29 33)}}
      {:name "Dexterity", :level 10, :type [:any], :effect {:agility (range 1 5)}}
      {:name "Agility", :level 20, :type [:any], :effect {:agility (range 5 9)}}
      {:name "Skill", :level 30, :type [:any], :effect {:agility (range 9 13)}}
      {:name "Accuracy", :level 40, :type [:any], :effect {:agility (range 13 17)}}
      {:name "Precision", :level 50, :type [:any], :effect {:agility (range 17 21)}}
      {:name "the Thief", :level 60, :type [:any], :effect {:agility (range 21 25)}}
      {:name "Perfection", :level 70, :type [:any], :effect {:agility (range 25 29)}}
      {:name "Nirvana", :level 80, :type [:any], :effect {:agility (range 29 33)}}
      {:name "the Apprentice", :level 10, :type [:any], :effect {:magic (range 1 5)}}
      {:name "the Novice", :level 20, :type [:any], :effect {:magic (range 5 9)}}
      {:name "Magic", :level 30, :type [:any], :effect {:magic (range 9 13)}}
      {:name "the Witch", :level 40, :type [:any], :effect {:magic (range 13 17)}}
      {:name "the Mage", :level 50, :type [:any], :effect {:magic (range 17 21)}}
      {:name "the Wizard", :level 60, :type [:any], :effect {:magic (range 21 25)}}
      {:name "the Sorcerer", :level 70, :type [:any], :effect {:magic (range 25 29)}}
      {:name "the Archimage", :level 80, :type [:any], :effect {:magic (range 29 33)}}
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
      {:name "Speed", :level 50, :type [:shield :ring], :effect {:evade (range 5 7)}}
      {:name "the Leech", :level 10, :type [:armor :shield :ring], :effect {:life-regen (range 10 31 10)}}
      {:name "the Locust", :level 30, :type [:armor :shield :ring], :effect {:life-regen (range 50 71 10)}}
      {:name "the Lamprey", :level 50, :type [:armor :shield :ring], :effect {:life-regen (range 80 101 10)}}])

(def *equips*
     [{:name "Short Sword", :type :weapon, :kind :sword, :level 5,
       :effect {:attack (range 1 6), :agility (range 1 3)}}
      {:name "Scimitar", :type :weapon, :kind :sword, :level 10,
       :effect {:attack (range 8 21), :agility (range 3 5)}}
      {:name "Falchion", :type :weapon, :kind :sword, :level 20,
       :effect {:attack (range 25 41), :agility (range 5 7)}}
      {:name "Long Sword", :type :weapon, :kind :sword, :level 30,
       :effect {:attack (range 45 61), :agility (range 7 9)}}
      {:name "Gladius", :type :weapon, :kind :sword, :level 40,
       :effect {:attack (range 65 81), :agility (range 9 11)}}
      {:name "Two-handed Sword", :type :weapon, :kind :sword, :level 50,
       :effect {:attack (range 85 101), :agility (range 11 13)}}
      {:name "Claymore", :type :weapon, :kind :sword, :level 60,
       :effect {:attack (range 110 131), :agility (range 13 15)}}
      {:name "Flamberge", :type :weapon, :kind :sword, :level 70,
       :effect {:attack (range 140 161), :agility (range 15 17)}}
      {:name "Great Sword", :type :weapon, :kind :sword, :level 80,
       :effect {:attack (range 170 181), :agility (range 17 19)}}
      {:name "Hell Blade", :type :weapon, :kind :sword, :level 90,
       :effect {:attack (range 180 201), :agility (range 18 21)}}
      {:name "Axe", :type :weapon, :kind :axe, :level 5,
       :effect {:attack (range 1 6), :strength (range 1 3)}}
      {:name "Double Axe", :type :weapon, :kind :axe, :level 10,
       :effect {:attack (range 8 21), :strength (range 3 5)}}
      {:name "War Axe", :type :weapon, :kind :axe, :level 20,
       :effect {:attack (range 25 41), :strength (range 5 7)}}
      {:name "Cleaver", :type :weapon, :kind :axe, :level 30,
       :effect {:attack (range 45 61), :strength (range 7 9)}}
      {:name "Tomahawk", :type :weapon, :kind :axe, :level 40,
       :effect {:attack (range 65 81), :strength (range 9 11)}}
      {:name "Large Axe", :type :weapon, :kind :axe, :level 50,
       :effect {:attack (range 85 101), :strength (range 11 13)}}
      {:name "Battle Axe", :type :weapon, :kind :axe, :level 60,
       :effect {:attack (range 110 131), :strength (range 13 15)}}
      {:name "Great Axe", :type :weapon, :kind :axe, :level 70,
       :effect {:attack (range 140 161), :strength (range 15 17)}}
      {:name "Giant Axe", :type :weapon, :kind :axe, :level 80,
       :effect {:attack (range 170 181), :strength (range 17 19)}}
      {:name "Ancient Axe", :type :weapon, :kind :axe, :level 90,
       :effect {:attack (range 180 201), :strength (range 18 21)}}
      {:name "Club", :type :weapon, :kind :mace, :level 5,
       :effect {:attack (range 1 6), :health (range 1 3)}}
      {:name "Spiked Club", :type :weapon, :kind :mace, :level 10,
       :effect {:attack (range 8 21), :health (range 3 5)}}
      {:name "Mace", :type :weapon, :kind :mace, :level 20,
       :effect {:attack (range 25 41), :health (range 5 7)}}
      {:name "Morning Star", :type :weapon, :kind :mace, :level 30,
       :effect {:attack (range 45 61), :health (range 7 9)}}
      {:name "Flail", :type :weapon, :kind :mace, :level 40,
       :effect {:attack (range 65 81), :health (range 9 11)}}
      {:name "War Hammer", :type :weapon, :kind :mace, :level 50,
       :effect {:attack (range 85 101), :health (range 11 13)}}
      {:name "Maul", :type :weapon, :kind :mace, :level 60,
       :effect {:attack (range 110 131), :health (range 13 15)}}
      {:name "Great Maul", :type :weapon, :kind :mace, :level 70,
       :effect {:attack (range 140 161), :health (range 15 17)}}
      {:name "Battle Hammer", :type :weapon, :kind :mace, :level 80,
       :effect {:attack (range 170 181), :health (range 17 19)}}
      {:name "Ogre Club", :type :weapon, :kind :mace, :level 90,
       :effect {:attack (range 180 201), :health (range 18 21)}}
      {:name "Short Staff", :type :weapon, :kind :staff, :level 5,
       :effect {:attack (range 1 6), :magic (range 1 3)}}
      {:name "Long Staff", :type :weapon, :kind :staff, :level 10,
       :effect {:attack (range 8 21), :magic (range 3 5)}}
      {:name "Battle Staff", :type :weapon, :kind :staff, :level 20,
       :effect {:attack (range 25 41), :magic (range 5 7)}}
      {:name "War Staff", :type :weapon, :kind :staff, :level 30,
       :effect {:attack (range 45 61), :magic (range 7 9)}}
      {:name "Quarterstaff", :type :weapon, :kind :staff, :level 40,
       :effect {:attack (range 65 81), :magic (range 9 11)}}
      {:name "Gothic Staff", :type :weapon, :kind :staff, :level 50,
       :effect {:attack (range 85 101), :magic (range 11 13)}}
      {:name "Rune Staff", :type :weapon, :kind :staff, :level 60,
       :effect {:attack (range 110 131), :magic (range 13 15)}}
      {:name "Elder Staff", :type :weapon, :kind :staff, :level 70,
       :effect {:attack (range 140 161), :magic (range 15 17)}}
      {:name "Magi Staff", :type :weapon, :kind :staff, :level 80,
       :effect {:attack (range 170 181), :magic (range 17 19)}}
      {:name "Heaven Staff", :type :weapon, :kind :staff, :level 90,
       :effect {:attack (range 180 201), :magic (range 18 21)}}
      {:name "Spear", :type :weapon, :kind :spear, :level 5,
       :effect {:attack (range 1 6), :magic (range 1 3)}}
      {:name "Trident", :type :weapon, :kind :spear, :level 10,
       :effect {:attack (range 8 21), :magic (range 3 5)}}
      {:name "Spetum", :type :weapon, :kind :spear, :level 20,
       :effect {:attack (range 25 41), :magic (range 5 7)}}
      {:name "Pike", :type :weapon, :kind :spear, :level 30,
       :effect {:attack (range 45 61), :magic (range 7 9)}}
      {:name "War Spear", :type :weapon, :kind :spear, :level 40,
       :effect {:attack (range 65 81), :magic (range 9 11)}}
      {:name "Lance", :type :weapon, :kind :spear, :level 50,
       :effect {:attack (range 85 101), :magic (range 11 13)}}
      {:name "Ancient Spear", :type :weapon, :kind :spear, :level 60,
       :effect {:attack (range 110 131), :magic (range 13 15)}}
      {:name "Mithril Spear", :type :weapon, :kind :spear, :level 70,
       :effect {:attack (range 140 161), :magic (range 15 17)}}
      {:name "Gothic Pike", :type :weapon, :kind :spear, :level 80,
       :effect {:attack (range 170 181), :magic (range 17 19)}}
      {:name "Holy Lance", :type :weapon, :kind :spear, :level 90,
       :effect {:attack (range 180 201), :magic (range 18 21)}}
      ;; chaotic set
      {:name "Chaotic Sword", :type :weapon, :kind :sword, :level 108,
       :effect {:attack (range 450 501), :strength (range 40 51), :agility (range 40 51), :critical (range 10 16)}}
      {:name "Chaotic Armor", :type :armor, :kind :full, :level 108,
       :effect {:defense (range 150 181), :health (range 40 51), :life (range 700 901)}}
      {:name "Chaotic Shield", :type :shield, :kind :great, :level 108,
       :effect {:defense (range 50 61), :evade (range 8 14), :mana (range 200 251), :hide (range 25 31)}}
      {:name "Chaotic Ring", :type :ring, :kind nil, :level 108,
       :effect {:life-regen (range 240 301 10), :mana-regen (range 420 541 20), :magic (range 40 51)}}
      ])

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
  (let [r (fn [m] (some #{(:level m)} (range (- level 50) (+ level 10))))
	name (rand-nth (filter r *prefixes*))]
    (get-prefix name)))

(defn- get-random-suffix [level]
  (let [r (fn [m] (some #{(:level m)} (range (- level 50) (+ level 10))))
	name (rand-nth (filter r *suffixes*))]
    (get-suffix name)))