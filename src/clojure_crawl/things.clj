(ns clojure-crawl.things)

(defrecord Effect [strength agility health magic attack defense critical evade life mana life-regen mana-regen hide skills])

(defrecord AFix [name type effect])

(defrecord Item [name description type kind prefix suffix level effect])