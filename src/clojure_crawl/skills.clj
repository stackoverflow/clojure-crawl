(ns clojure-crawl.skills
  (:import (clojure-crawl.actors Skill))
  (:use clojure-crawl.actors
	clojure-crawl.utils))

(defmacro gen-skill-methods [name desc consu st ag he ma at de cr ev li mn lr mr hi]
  `(do (defmethod ~'mana-consume ~name [~'skill ~'actor] ~consu)
       (defmethod ~'describe-skill ~name [~'skill ~'actor] ~desc)
       (defmethod ~'skill-strength ~name [~'skill ~'actor] ~st)
       (defmethod ~'skill-agility ~name [~'skill ~'actor] ~ag)
       (defmethod ~'skill-health ~name [~'skill ~'actor] ~he)
       (defmethod ~'skill-magic ~name [~'skill ~'actor] ~ma)
       (defmethod ~'skill-attack ~name [~'skill ~'actor] ~at)
       (defmethod ~'skill-defense ~name [~'skill ~'actor] ~de)
       (defmethod ~'skill-critical ~name [~'skill ~'actor] ~cr)
       (defmethod ~'skill-evade ~name [~'skill ~'actor] ~ev)
       (defmethod ~'skill-life ~name [~'skill ~'actor] ~li)
       (defmethod ~'skill-mana ~name [~'skill ~'actor] ~mn)
       (defmethod ~'skill-life-regen ~name [~'skill ~'actor] ~lr)
       (defmethod ~'skill-mana-regen ~name [~'skill ~'actor] ~mr)
       (defmethod ~'skill-hide ~name [~'skill ~'actor] ~hi)))

(def *skills*
     {:crush {:name "Crush" :description "Powerfull attack"
	      :only-in-battle? true :active? true :target :enemy}
      :critical-strike {:name "Critical Strike" :description "Improves attack and critical %"
			:only-in-battle? true :active? true :target :enemy}
      :fireball {:name "Fireball" :description "Great ball of fire"
		 :only-in-battle? true :active? true :target :enemy}
      :heal {:name "Heal" :description "Heals life"
	     :only-in-battle? false :active? true :target :self}
      :attack-up {:name "Attack Up" :description "Increases attack power"
		  :only-in-battle? false :active? false :target nil}
      :sneak {:name "Sneak" :description "Increases hide %"
	      :only-in-battle? false :active? false :target nil}
      :magic-up {:name "Magic Up" :description "Increases magic power"
		 :only-in-battle? false :active? false :target nil}
      :vitality {:name "Vitality" :description "Increases life"
		 :only-in-battle? false :active? false :target nil}})

(defn new-skill [skill-map level]
  (new Skill (:name skill-map) (:description skill-map)
       (:only-in-battle? skill-map) (:active? skill-map)
       (atom level) (:target skill-map) (atom 0)))

(defn can-use [actor skill]
  (let [consume (mana-consume skill actor)
	mana @(:mana actor)]
    (>= mana consume)))

(gen-skill-methods "Crush"
		   (let [level @(:level skill)			 
			 tmp (+ 1 (/ level 10))]
		     {:attack (apply-in-vec #(+ 10 (* tmp %)) (base-attack actor)),
		      :critical (critical actor), :mana (* 5 level)})
		   (* 5 @(:level skill)) ;consume
		   0 0 0 0
		   (let [bonus (+ 1 (/ @(:level skill) 10))]
		     (+ 10 (* bonus (attack actor)))) ;attack
		   0 0 0 0 0 0 0 0)

(gen-skill-methods "Critical Strike"
		   (let [level @(:level skill)
			 batt (base-attack actor)
			 crit (critical actor)]
		     {:attack (apply-in-vec #(+ (* 5 level) %) batt),
		      :critical (+ (* 5 level) crit), :mana (* 5 level)})		   
		   (* 5 @(:level skill)) ;consume
		   0 0 0 0
		   (+ (* 5 @(:level skill)) (attack actor)) ;attack
		   0
		   (+ (* 5 @(:level skill)) (critical actor)) ;critical
		   0 0 0 0 0 0)

(gen-skill-methods "Fireball"
		   (let [level @(:level skill)
			 tmp (+ 1 (/ level 10))
			 mag @(:magic actor)
			 m2 (* mag 0.75)]
		     {:attack [(* tmp m2) (* tmp mag)], :mana (* 5 level)})		   
		   (* 5 @(:level skill)) ;consume
		   0 0 0 0
		   (let [mag @(:magic actor)
			 m3 (* mag 0.75)			 
			 bonus (+ 1 (/ @(:level skill) 10))]
		     (* bonus (rand-between m3 mag))) ;attack (magic)
		   0 0 0 0 0 0 0 0)

(gen-skill-methods "Heal"
		   (let [level @(:level skill)
			 tmp (+ 1 (/ level 10))
			 mag @(:magic actor)
			 m2 (* mag 0.75)]
		     {:attack [(* tmp m2) (* tmp mag)], :mana (* 5 level)})		   
		   (* 5 @(:level skill)) ;consume
		   0 0 0 0
		   (let [mag @(:magic actor)
			 m3 (* mag 0.75)			 
			 bonus (+ 1 (/ @(:level skill) 10))]
		     (* bonus (rand-between m3 mag))) ;attack (magic)
		   0 0 0 0 0 0 0 0)

(gen-skill-methods "Attack Up"
		   {:attack @(:level actor)}
		   0 0 0 0 0
		   @(:level actor) ;attack
		   0 0 0 0 0 0 0 0)

(gen-skill-methods "Sneak"
		   {:hide (/ @(:level actor) 2)}
		   0 0 0 0 0 0 0 0 0 0 0 0 0
		   (/ @(:level actor) 2)) ;hide

(gen-skill-methods "Magic Up"
		   {:magic @(:level actor)}
		   0 0 0 0
		   @(:level actor) ;magic
		   0 0 0 0 0 0 0 0 0)

(gen-skill-methods "Vitality"
		   {:life (* 3 @(:level actor))}
		   0 0 0 0 0 0 0 0 0
		   (* 3 @(:level actor)) ;life
		   0 0 0 0)