(ns clojure-crawl.levels
  (:use clojure-crawl.actors))

(def *xp-table*
     (for [x (range 1 100)] (int (* x x 10))))