(set-env!
 :source-paths #{"src"}
 :resource-paths #{"html"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [adzerk/boot-cljs "1.7.228-1"]
                 [pandeiro/boot-http "0.7.3"]
                 [adzerk/boot-reload "0.4.8"]
                 [adzerk/boot-cljs-repl "0.3.0"]
                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                 [weasel "0.7.0"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [cljsjs/three "0.0.70-0"]
                 [cljsjs/react-slider "0.6.1-0"]
                 [rum "0.9.0"]
                 [http-kit "2.1.18"]
                 [org.clojure/tools.logging "0.3.1"]
                 [com.domkm/silk "0.1.2"]
                 [ring/ring-core "1.5.0"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]])

(task-options!
 repl {:middleware '[cemerick.piggieback/wrap-cljs-repl]}
 pom {:project 'pong :version "0.1.0"}
 jar {:main 'server.web
      :manifest {"Description" "PONG in THREE.js - Built in ClojureScript"
                 "Url" "https://github.com/volrath/clojurescript-pong-3d"}})

(deftask dev
  "Launch Immediate Feedback Development Environment"
  []
  (comp
   (serve :handler 'server.web/app
          :resource-root "target"
          :reload true
          :httpkit true)
   (watch)
   (reload)
   (cljs-repl)
   (cljs)
   (target :dir #{"target"})))

(deftask build
  "Builds an advanced compiled version of the app"
  []
  (comp
   (cljs :optimizations :advanced)
   (aot :namespace '#{server.web})
   (pom)
   (uber)
   (jar)
   (target :dir #{"release"})))
