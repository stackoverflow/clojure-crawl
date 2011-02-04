(ns clojure-crawl.saver
  (:import (java.io File StringWriter))
  (:use clojure.contrib.pprint)
  (:require [clojure.contrib [io :as io]]))

(def ^{:private true} file-name "clojure_crawl_save-%s.save")
(def ^{:private true} home (System/getProperty "user.home"))
(def ^{:private true} dir-name (str home "/clojure_crawl"))

(defn- mkdir []
  (.mkdir (io/file-str dir-name)))

(defn save [game-state slot]
  (mkdir)
  (spit (io/file-str dir-name "/" (format file-name slot))
	(pr-str game-state)))