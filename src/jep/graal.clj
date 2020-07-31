(ns jep.graal
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.java.shell :as sh])
  (:import (jep MainInterpreter
                Interpreter
                SharedInterpreter)
           (java.io File))
  (:gen-class))

(def ^:dynamic *python* (or (System/getenv "JEP_PYTHON") "python"))

(def ^:dynamic *pip* (or (System/getenv "JEP_PIP") "pip"))

(def ^:dynamic *registry* (atom {}))

(defn bootstrap-code
  ([]
   ["import sys"
    "import site "
    "import subprocess"
    "import os"
    "import glob"
    ""
    "if sys.version[0] != '3':"
    "  print(\"Requires Python 3\")"
    "  exit(1)"
    ""
    "def check_jep ():"
    "  return glob.glob(os.path.join(site.getsitepackages()[0], \"jep/libjep.*\"))"
    ""
    "jep_out = check_jep()"
    ""
    "if len(jep_out) == 0:"
    "  print(\"Jep not present, Installing...\")"
    (format "  subprocess.call(['%s', 'install', 'jep'])" *pip*)
    "  jep_out = check_jep()"
    ""
    "print(jep_out[0])"]))

(defn jep-bootstrap
  "returns the jep runtime
 
   (jep-bootstrap)
   => string?"
  {:added "3.0"}
  ([]
   (let [path (File/createTempFile "jep" ".py")
         _    (spit path (str/join "\n" (bootstrap-code)))
         {:keys [exit out]} (sh/sh *python* (str path))]
     (if (zero? exit)
       (last (str/split-lines out))
       (throw (ex-info out {:status :failed :message out}))))))

(defn init-paths
  "sets the path of the jep interpreter"
  {:added "3.0"}
  ([]
   (let [jep-path  (jep-bootstrap)
         root (str/replace jep-path #"/jep/libjep.*" "")]
     (MainInterpreter/setJepLibraryPath jep-path)
     (SharedInterpreter/setConfig
      (-> (jep.JepConfig.)
          (.setRedirectOutputStreams true)
          (.addIncludePaths (into-array [root])))))))

(defn get-interpreter
  ([]
   (let [thread (Thread/currentThread)]
     (or (get @*registry* thread)
         (doto (jep.SharedInterpreter.)
           (-> ((fn [session]
                  (swap! *registry* assoc thread session)))))))))

(def +init+
  (delay (init-paths)))

(defn -main
  "I don't do a whole lot."
  ([]
   @+init+
   
   (println "TEST EXAMPLE: a = 1 + 1, return a")
   (-> (get-interpreter)
       (doto (.exec  "a = 1 + 1"))
       (.getValue "a")
       (println))
   (.close (get-interpreter))
   (System/exit 0)))

(comment
  (binding [*python* "python3"
            *pip* "python3"])
  (-main))


