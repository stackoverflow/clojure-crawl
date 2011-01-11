(ns clojure-crawl.game
  (:use clojure-crawl.utils
	clojure-crawl.races
	clojure-crawl.classes
	clojure-crawl.skills)
  (:import (clojure-crawl.actors Player))
  (:require [clojure [string :as string]]
	    [clojure-crawl [map :as gamemap]]
	    [clojure-crawl [levels :as levels]]
	    [clojure-crawl [actors :as actors]]))

(defprotocol IGame
  (set-player [game player])
  (set-dungeon [game dungeon])
  (set-current-level [game current-level])
  (set-current-room [game current-room])
  (player-attack [game])
  (enemy-attack [game]))

(defn- attack* [att vic]
  (let [vic-evd? (probability-result (actors/evade vic))
	att-cri? (probability-result (actors/critical att))
	defen (actors/defense vic)
	damage (pos-num (actors/attack att))]
    {:damage (if att-cri?
	       (- (* 2 damage) defen)
	       (- damage defen))
     :critical (if vic-evd? false att-cri?)
     :evade vic-evd?}))

(defn- use-skill* [skill att vic]
  (let [target (:target skill)
	desc (actors/describe-skill skill att)]
    (cond (= target :enemy)
	  (let [crit (:critical desc)
		crit? (probability-result (if crit crit 0))
		dmg (if (:attack desc)
		      (actors/skill-attack skill att)
		      0)
		defen (actors/defense vic)]
	    {:damage (if crit?
		       (- (* 2 dmg) defen)
		       (- dmg defen))
	     :critical crit?})
	  (= target :self)
	  (let [crit (:critical desc)
		crit? (probability-result (if crit crit 0))
		dmg (if (:attack desc)
		      (actors/skill-attack skill att)
		      0)]
	    {:damage (if crit? (* 2 (- dmg)) (- dmg))
	     :critical crit?}))))

(defrecord Game [player dungeon current-level current-room])

(extend-type Game
  IGame
  (set-player [game player]
	      (reset! (:player game) player))
  (set-dungeon [game dungeon]
	      (reset! (:dungeon game) dungeon))
  (set-current-level [game current-level]
	      (reset! (:current-level game) current-level))
  (set-current-room [game current-room]
		    (reset! (:current-room game) current-room))
  (player-attack [game]
	     (let [player @(:player game)
		   enemy (:enemy @(:current-room game))]
	       (when (and enemy
			  (not (actors/dead? enemy)))
		 (let [res (attack* player enemy)]
		   (when (not (:evade res))
		     (actors/damage enemy (:damage res)))
		   res))))
  (enemy-attack [game]
	     (let [player @(:player game)
		   enemy (:enemy @(:current-room game))]
	       (when (and enemy
			  (not (actors/dead? enemy)))
		 (let [res (attack* enemy player)]
		   (when (not (:evade res))
		     (actors/damage player (:damage res)))
		   res)))))

(def game (new Game (atom nil) (atom nil) (atom nil) (atom nil)))

;; create player

(defn- add-race-bonus [item race value]
  (+ (item (:effect race)) value))

(defn- life-skill-bonus [skills actor]
  (let [pass (filter #(not (:active? %)) skills)]
    (reduce + (map #(actors/skill-life % actor) skills))))

(defn- mana-skill-bonus [skills actor]
  (let [pass (filter #(not (:active? %)) skills)]
    (reduce + (map #(actors/skill-mana % actor) skills))))

(defn new-player [name race clazz]
  (let [r (race *races*)
	c (clazz *classes*)
	skills (vec (map #(new-skill (% *skills*) 1) (:skills c)))
	health (add-race-bonus :health r (* 5 (:health c)))
	magic (add-race-bonus :magic r (* 5 (:magic c)))
	life (add-race-bonus :life r (+ 20 (* 2 health)))
	mana (add-race-bonus :mana r (* 2 magic))
	player (new Player name r (atom c)
		    (atom (add-race-bonus :strength r (* 5 (:strength c))))
		    (atom (add-race-bonus :agility r (* 5 (:agility c))))
		    (atom health)
		    (atom magic)
		    (atom skills)
		    (atom 0) ;exp
		    (atom life)
		    (atom life) ;max life
		    (atom mana)
		    (atom mana) ;max mana
		    (atom 1) ; level
		    (atom {}) ;equip
		    (atom []) ;bag
		    (atom []))] ;effects
    (swap! (:life player) + (life-skill-bonus skills player))
    (swap! (:mana player) + (mana-skill-bonus skills player))
    player))

;; game
(defn start-game [pname race clazz]
  (let [player (new-player pname (name->key race) (name->key clazz))
	level (gamemap/enter-dungeon)]
    (set-player game player)
    (set-dungeon game @gamemap/dungeon)
    (set-current-level game level)
    (set-current-room game @gamemap/current-room)))

(defn can-go? [side]
  (let [room @gamemap/current-room
	enemy (:enemy room)]
    (if (side room)
      (if enemy
	(if (or (actors/dead? enemy)
		(not (:aware enemy)))
	  true
	  false)
	true)
      false)))

(defn go [side]
  (when (can-go? side)
    (gamemap/go side)
    (reset! (:current-room game) @gamemap/current-room)
    (when-let [enemy (:enemy @gamemap/current-room)]
      (let [p (probability-result (actors/hide @(:player game)))]
	(when-not p
	  (reset! (:aware enemy) true))))))

(defn ascend []
  (when-let [room @(:current-room game)]
    (gamemap/ascend)
    (reset! (:current-room game) @gamemap/current-room)
    (reset! (:current-level game) @gamemap/current-level)))

(defn descend []
  (when-let [room @(:current-room game)]
    (gamemap/descend)
    (reset! (:current-room game) @gamemap/current-room)
    (reset! (:current-level game) @gamemap/current-level)))

(defn current-enemy []
  (:enemy @(:current-room game)))

(defn current-room []
  @(:current-room game))

(defn player []
  @(:player game))

(defn- give-xp-skill [skill player res]
  (actors/add-skill-xp player skill (levels/skill-xp-for-level))
  (if (levels/skill-leveled? skill)
    (do
      (levels/skill-level-up skill)
      (assoc res :skill skill :skill-level-up true))
    (assoc res :skill skill)))

(defn- give-xp [player enemy res]
  (if (actors/dead? enemy)
    (let [xp (levels/xp-for-level (:level enemy))]
      (actors/add-xp player xp)
      (if (levels/leveled? player)
	(do
	  (levels/level-up player)
	  (assoc res :exp xp :level-up true))
	(assoc res :exp xp)))
    res))

(defn attack-enemy []
  (let [enemy (current-enemy)
	res (player-attack game)
	player (player)]
    (give-xp player enemy res)))

(defn attack-player []
  (enemy-attack game))

(defn- use-skill [skill att vic give-xp?]
  (let [consume (actors/mana-consume skill att)]
    (if (can-use att skill)
      (let [res (use-skill* skill att vic)
	    target (:target skill)]
	(cond (= target :enemy)
	      (actors/damage vic (:damage res))
	      (= target :self)
	      (actors/damage att (:damage res)))
	(actors/consume-mana att consume)
	(if give-xp?
	  (give-xp att vic (give-xp-skill skill att res))
	  (assoc res :skill skill)))
      nil)))

(defn use-skill-player [name]
  (when-let [skill (first (filter #(= name (:name %)) @(:skills (player))))]
    (let [pl (player)
	  enemy (current-enemy)]
      (use-skill skill pl enemy true))))

(defn use-skill-enemy [skill]
  (let [pl (player)
	enemy (current-enemy)]
    (use-skill skill enemy pl false)))

(defn pickup-item
  ([] (pickup-item @(:treasure (current-room))))
  ([item]
     (if item
       (do
	 (reset! (:treasure (current-room)) nil)
	 (let [add (actors/add-item (player) item)]
	   (if add
	     :added
	     :full)))
       :no-item)))

(defn reset []
  (reset! (:player game) nil)
  (reset! (:dungeon game) nil)
  (reset! (:current-level game) nil)
  (reset! (:current-room game) nil)
  (gamemap/reset))

(defn remove-item [item]
  (actors/remove-item (player) item))

(defn equip-item [item]
  (actors/equip-item (player) item))

(defn unequip-item [item]
  (actors/unequip-item (player) item))

;; helpers
(defn show-player [player]
  (str "===== " (:name player) " =====\n"
       "race: " (:name (:race player)) "\n"
       "class: " (:name @(:clazz player)) "\n\n"
       "strength: " (actors/strength player) "\n"
       "agility: " (actors/agility player) "\n"
       "health: " (actors/health player) "\n"
       "magic: " (actors/magic player) "\n"
       "life: " @(:life player) "/" (actors/max-life player) "\n"
       "mana: " @(:mana player) "/" (actors/max-mana player) "\n"
       "life regen: " (double (actors/life-regen player)) " per second\n"
       "mana regen: " (double (actors/mana-regen player)) " per second\n"
       "skills: " (string/join ", " (map :name @(:skills player)))))

(defn- target->str [target]
  (cond (= target :enemy)
	"enemy"
	(= target :self)
	"self"
	:default "-"))

(defn- passive-skill-desc->str [desc]
  (let [f (fn [[k v]]
	    (let [value (if (or (= k :hide)
				(= k :critical)
				(= k :evade))
			  (str (to-num v) "%")
			  (to-num v))]
	      (str "adds " value " to " (key->name k) "\n")))]
    (apply str (map f desc))))

(defn- active-skill-desc->str [desc]
  (let [f (fn [[k v]]
	    (cond (= k :attack)
		  (str "damage: " (to-num (first v)) " - " (to-num (second v)) "\n")
		  (= k :critical)
		  (str "critical chance: " (to-num v) "%\n")
		  (= k :mana)
		  (str "mana: " v "\n")))]
    (apply str (map f desc))))

(defn- skill-desc->str [desc active]
  (if active
    (active-skill-desc->str desc)
    (passive-skill-desc->str desc)))

(defn show-skill [name player]
  (let [skill (first (filter #(= name (:name %)) @(:skills player)))
	desc (actors/describe-skill skill player)]
    (str (:name skill) "\n"
	 (:description skill) "\n"
	 (if (:active? skill)
	   (str "level: " @(:level skill) "\n"))
	 "active: " (if (:active? skill) "yes" "no") "\n"
	 "only in battle: " (if (:only-in-battle? skill) "yes" "no") "\n"
	 "target: " (target->str (:target skill)) "\n"
	 (when (:active? skill)
	   (str "exp: " @(:exp skill) "/" (levels/skill-next-level-xp skill) "\n"))
	 (skill-desc->str desc (:active? skill)))))