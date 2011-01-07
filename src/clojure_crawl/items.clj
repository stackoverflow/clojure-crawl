(ns clojure-crawl.items
  (:use clojure-crawl.utils)
  (:import (clojure-crawl.things Item AFix))
  (:require [clojure-crawl [things :as things]]))

(def *fix-chance* 25)

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
      {:name "Bat", :level 10, :type [:armor :shield :ring], :effect {:mana-regen (range 10 31 10)}}
      {:name "Wraith", :level 30, :type [:armor :shield :ring], :effect {:mana-regen (range 40 61 10)}}
      {:name "Vampire", :level 50, :type [:armor :shield :ring], :effect {:mana-regen (range 70 91 10)}}
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
      {:name "the Colossus", :level 60, :type [:armor :shield :ring], :effect {:life (range 170 220)}}
      {:name "the Squid", :level 70, :type [:armor :shield :ring], :effect {:life (range 240 290)}}
      {:name "the Whale", :level 80, :type [:armor :shield :ring], :effect {:life (range 300 350)}}
      {:name "Evade", :level 10, :type [:shield :ring], :effect {:evade (range 1 3)}}
      {:name "Dodge", :level 30, :type [:shield :ring], :effect {:evade (range 3 5)}}
      {:name "Speed", :level 50, :type [:shield :ring], :effect {:evade (range 5 7)}}
      {:name "the Leech", :level 10, :type [:armor :shield :ring], :effect {:life-regen (range 5 16 5)}}
      {:name "the Locust", :level 30, :type [:armor :shield :ring], :effect {:life-regen (range 20 31 5)}}
      {:name "the Lamprey", :level 50, :type [:armor :shield :ring], :effect {:life-regen (range 35 46 5)}}])

(def *equips*
     ;; weapons
     [{:name "Short Sword", :type :weapon, :kind :sword, :level 5,
       :effect {:attack (range 1 6), :agility (range 1 3)}}
      {:name "Scimitar", :type :weapon, :kind :sword, :level 15,
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
      {:name "Double Axe", :type :weapon, :kind :axe, :level 15,
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
      {:name "Forgotten Axe", :type :weapon, :kind :axe, :level 90,
       :effect {:attack (range 180 201), :strength (range 18 21)}}
      {:name "Club", :type :weapon, :kind :mace, :level 5,
       :effect {:attack (range 1 6), :health (range 1 3)}}
      {:name "Spiked Club", :type :weapon, :kind :mace, :level 15,
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
      {:name "Might Hammer", :type :weapon, :kind :mace, :level 80,
       :effect {:attack (range 170 181), :health (range 17 19)}}
      {:name "Ogre Club", :type :weapon, :kind :mace, :level 90,
       :effect {:attack (range 180 201), :health (range 18 21)}}
      {:name "Short Staff", :type :weapon, :kind :staff, :level 5,
       :effect {:attack (range 1 6), :magic (range 1 3)}}
      {:name "Long Staff", :type :weapon, :kind :staff, :level 15,
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
      {:name "Trident", :type :weapon, :kind :spear, :level 15,
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
      {:name "Dagger", :type :weapon, :kind :dagger, :level 5,
       :effect {:attack (range 1 5), :critical (range 1 2)}}
      {:name "Dirk", :type :weapon, :kind :dagger, :level 15,
       :effect {:attack (range 7 18), :critical (range 1 3)}}
      {:name "Kris", :type :weapon, :kind :dagger, :level 20,
       :effect {:attack (range 20 31), :critical (range 2 3)}}
      {:name "Blade", :type :weapon, :kind :dagger, :level 30,
       :effect {:attack (range 35 51), :critical (range 2 4)}}
      {:name "Stiletto", :type :weapon, :kind :dagger, :level 40,
       :effect {:attack (range 55 71), :critical (range 3 4)}}
      {:name "Mithril Dagger", :type :weapon, :kind :dagger, :level 50,
       :effect {:attack (range 75 101), :critical (range 3 5)}}
      {:name "Mithril Blade", :type :weapon, :kind :dagger, :level 60,
       :effect {:attack (range 110 131), :critical (range 4 5)}}
      {:name "Diamond Dagger", :type :weapon, :kind :dagger, :level 70,
       :effect {:attack (range 130 141), :critical (range 4 6)}}
      {:name "Blood Kris", :type :weapon, :kind :dagger, :level 80,
       :effect {:attack (range 150 161), :critical (range 5 6)}}
      {:name "Hell Dagger", :type :weapon, :kind :dagger, :level 90,
       :effect {:attack (range 170 181), :critical (range 5 7)}}
      {:name "Short Bow", :type :weapon, :kind :bow, :level 5,
       :effect {:attack (range 1 6), :evade (range 1 2)}}
      {:name "Bow", :type :weapon, :kind :bow, :level 15,
       :effect {:attack (range 8 21), :evade (range 1 3)}}
      {:name "Long Bow", :type :weapon, :kind :bow, :level 20,
       :effect {:attack (range 25 41), :evade (range 2 3)}}
      {:name "Composite Bow", :type :weapon, :kind :bow, :level 30,
       :effect {:attack (range 45 61), :evade (range 2 4)}}
      {:name "War Bow", :type :weapon, :kind :bow, :level 40,
       :effect {:attack (range 65 81), :evade (range 3 4)}}
      {:name "Long War Bow", :type :weapon, :kind :bow, :level 50,
       :effect {:attack (range 85 101), :evade (range 3 5)}}
      {:name "Mithril Bow", :type :weapon, :kind :bow, :level 60,
       :effect {:attack (range 110 131), :evade (range 4 5)}}
      {:name "Diamond Bow", :type :weapon, :kind :bow, :level 70,
       :effect {:attack (range 140 161), :evade (range 4 6)}}
      {:name "Blood Bow", :type :weapon, :kind :bow, :level 80,
       :effect {:attack (range 170 181), :evade (range 5 6)}}
      {:name "Energy Bow", :type :weapon, :kind :bow, :level 90,
       :effect {:attack (range 180 201), :evade (range 5 7)}}
      ;; armors
      {:name "Coat", :type :armor, :kind :leather, :level 5,
       :effect {:defense (range 1 5)}}
      {:name "Leather Jacket", :type :armor, :kind :leather, :level 13,
       :effect {:defense (range 4 10)}}
      {:name "Leather Armor", :type :armor, :kind :leather, :level 17,
       :effect {:defense (range 9 15)}}
      {:name "Hard Leather Armor", :type :armor, :kind :leather, :level 20,
       :effect {:defense (range 14 20)}}
      {:name "Studded Leather", :type :armor, :kind :leather, :level 25,
       :effect {:defense (range 19 25)}}
      {:name "Demonskin Armor", :type :armor, :kind :leather, :level 30,
       :effect {:defense (range 24 30)}}
      {:name "Chain Shirt", :type :armor, :kind :metal, :level 35,
       :effect {:defense (range 29 35)}}
      {:name "Ring Armor", :type :armor, :kind :metal, :level 40,
       :effect {:defense (range 34 40)}}
      {:name "Chain Mail", :type :armor, :kind :metal, :level 45,
       :effect {:defense (range 39 45)}}
      {:name "Scale Mail", :type :armor, :kind :metal, :level 50,
       :effect {:defense (range 44 50)}}
      {:name "Breastplate Armor", :type :armor, :kind :metal, :level 55,
       :effect {:defense (range 49 55)}}
      {:name "Splint Mail", :type :armor, :kind :metal, :level 60,
       :effect {:defense (range 54 60)}}
      {:name "Mithril Shirt", :type :armor, :kind :metal, :level 65,
       :effect {:defense (range 59 65)}}
      {:name "Plate Mail", :type :armor, :kind :full, :level 70,
       :effect {:defense (range 64 71)}}
      {:name "Half Plate", :type :armor, :kind :full, :level 75,
       :effect {:defense (range 69 77)}}
      {:name "Field Plate", :type :armor, :kind :full, :level 80,
       :effect {:defense (range 75 83)}}
      {:name "Gothic Armor", :type :armor, :kind :full, :level 85,
       :effect {:defense (range 81 89)}}
      {:name "Full Plate Mail", :type :armor, :kind :full, :level 90,
       :effect {:defense (range 87 95)}}
      {:name "Glorious Armor", :type :armor, :kind :full, :level 95,
       :effect {:defense (range 93 101)}}
      ;; shields
      {:name "Buckler", :type :shield, :kind :leather, :level 5,
       :effect {:defense (range 1 4)}}
      {:name "Leather Shield", :type :shield, :kind :leather, :level 15,
       :effect {:defense (range 3 8)}}
      {:name "Spiked Shield", :type :shield, :kind :leather, :level 20,
       :effect {:defense (range 6 11)}}
      {:name "Iron Shield", :type :shield, :kind :metal, :level 30,
       :effect {:defense (range 8 13)}}
      {:name "Steel Shield", :type :shield, :kind :metal, :level 40,
       :effect {:defense (range 11 16)}}
      {:name "Mithril Shield", :type :shield, :kind :metal, :level 50,
       :effect {:defense (range 15 20)}}
      {:name "Paladin Shield", :type :shield, :kind :metal, :level 60,
       :effect {:defense (range 18 23)}}
      {:name "Tower Shield", :type :shield, :kind :full, :level 70,
       :effect {:defense (range 22 27)}}
      {:name "Giant Shield", :type :shield, :kind :full, :level 80,
       :effect {:defense (range 25 30)}}
      {:name "Defender", :type :shield, :kind :full, :level 90,
       :effect {:defense (range 29 34)}}
      ;; rings (they don't give any bonus other than their prefix/suffix)
      {:name "Ring", :type :ring, :kind nil, :level 5, :effect nil}
      {:name "Ring", :type :ring, :kind nil, :level 50, :effect nil}
      ;; chaos set
      {:name "Chaos Sword", :type :weapon, :kind :sword, :level 108,
       :effect {:attack (range 450 501), :strength (range 40 51), :agility (range 40 51), :critical (range 10 16)}}
      {:name "Chaos Armor", :type :armor, :kind :full, :level 108,
       :effect {:defense (range 150 181), :health (range 40 51), :life (range 700 901)}}
      {:name "Chaos Shield", :type :shield, :kind :great, :level 108,
       :effect {:defense (range 50 61), :evade (range 8 14), :mana (range 200 251), :hide (range 25 31)}}
      {:name "Chaos Ring", :type :ring, :kind nil, :level 108,
       :effect {:life-regen (range 240 301 10), :mana-regen (range 420 541 20), :magic (range 40 51)}}])

(def *items*
     [{:name "Small Potion", :type :item, :kind :potion, :level 10,
       :effect {:life (range 10 30)}}
      {:name "Potion", :type :item, :kind :potion, :level 20,
       :effect {:life (range 130 180)}}
      {:name "Medium Potion", :type :item, :kind :potion, :level 40,
       :effect {:life (range 250 300)}}
      {:name "Big Potion", :type :item, :kind :potion, :level 60,
       :effect {:life (range 420 470)}}
      {:name "Super Potion", :type :item, :kind :potion, :level 90,
       :effect {:life (range 750 800)}}
      {:name "Small Mana Potion", :type :item, :kind :potion, :level 10,
       :effect {:mana (range 8 20)}}
      {:name "Mana Potion", :type :item, :kind :potion, :level 20,
       :effect {:mana (range 25 33)}}
      {:name "Medium Mana Potion", :type :item, :kind :potion, :level 40,
       :effect {:mana (range 45 55)}}
      {:name "Big Mana Potion", :type :item, :kind :potion, :level 60,
       :effect {:mana (range 65 80)}}
      {:name "Super Mana Potion", :type :item, :kind :potion, :level 90,
       :effect {:mana (range 100 120)}}
      {:name "Mixed Potion", :type :item, :kind :potion, :level 20,
       :effect {:life (range 90 120), :mana (range 16 22)}}
      {:name "Big Mixed Potion", :type :item, :kind :potion, :level 50,
       :effect {:life (range 280 313), :mana (range 43 53)}}
      {:name "Super Mixed Potion", :type :item, :kind :potion, :level 80,
       :effect {:life (range 500 533), :mana (range 66 80)}}])

(defn- add-effects [& effects]
  (things/create-effect (apply merge-with + effects)))

(defn- range-fn [level]
  (fn [m] (some #{(:level m)} (range (- level 50) (+ level 10)))))

(defn- gen-random-map [[k v]]
  (if (seq? v)
    [k (rand-nth v)]
    [k v]))

(defn- get-random-fix [level which type]
  (let [coll (if (= which :prefix) *prefixes* *suffixes*)
	filt (filter #(some #{type :any} (:type %)) coll)
	fix (rand-nth (filter (range-fn level) filt))
	eff (things/create-effect (into {} (map gen-random-map (:effect fix))))]
    (new AFix (:name fix) which (:level fix) eff)))

(defn- get-random-prefix [level type]
  (get-random-fix level :prefix type))

(defn- get-random-suffix [level type]
  (get-random-fix level :suffix type))

(defn gen-random-item [level]
  (let [equip? (probability-result 50)]
    (if (not equip?)
      (let [item (rand-nth (filter (range-fn level) *items*))]
	(new Item (:name item) (:type item) (:kind item) nil nil (:level item)
	     (things/create-effect (into {} (map gen-random-map (:effect item))))))
      (let [type (rand-nth things/*equip-types*)
	    prefix? (if (= type :ring)
		      (probability-result 50)
		      (probability-result *fix-chance*))
	    suffix? (if (= type :ring)
		      (if prefix?
			(probability-result 50)
			true)
		      (probability-result *fix-chance*))
	    prefix (when prefix? (get-random-prefix level type))
	    suffix (when suffix? (get-random-suffix level type))
	    itemtype (filter #(= (:type %) type) *equips*)
	    item (rand-nth (filter (range-fn level) itemtype))]
	(new Item (:name item) type (:kind item) prefix suffix (:level item)
	     (things/create-effect (into {} (map gen-random-map (:effect item)))))))))

(defn item-name [item]
  (let [p (:prefix item)
	s (:suffix item)]
    (str (when p (str (:name p) " "))
	 (:name item)
	 (when s (str " of " (:name s))))))

(defn show-item [item]
  (let [add (fn [& ns]
	      (if (empty? (filter identity ns))
		nil
		(apply + (filter identity ns))))
	p (:prefix item)
	s (:suffix item)
	ifx (:effect item)
	fx (cond (and (not p) (not s))
		 ifx
		 (and p (not s))
		 (merge-with add ifx (:effect p))
		 (and s (not p))
		 (merge-with add ifx (:effect s))
		 (and p s)
		 (merge-with + ifx (:effect p) (:effect s)))]
    (str (item-name item) "\n"
	 "Type: " (key->name (:type item)) "\n"
	 "Kind: " (key->name (:kind item)) "\n"
	 (things/show-effect fx))))