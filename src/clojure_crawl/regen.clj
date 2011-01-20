(ns clojure-crawl.regen
  (:require [clojure-crawl [game :as game]]
	    [clojure-crawl [actors :as actors]]))

(def ^{:private true} function (atom nil))

(def ^{:private true} on (atom false))

(defn set-repaint [f]
  (reset! function f))

(defn on []
  (reset! on true))

(defn off []
  (reset! on false))

(defn- execute []
  (when @on
    (let [player (game/player)]
      (actors/regen-life player)
      (actors/regen-mana player))))