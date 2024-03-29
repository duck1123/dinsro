(ns serve
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [clojure.tools.cli :refer [parse-opts]]
   [hiccup2.core :as html]
   [org.httpkit.server :as server])
  (:import [java.net URLDecoder URLEncoder]))

(def cli-options
  ;; An option with a required argument
  [["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-h" "--help"]])

(def parsed-args (parse-opts *command-line-args* cli-options))
(def opts (:options parsed-args))
(prn parsed-args)

(def port (:port opts))
(def dir (fs/path (or (second (:arguments parsed-args)) "/usr/src/app/docs")))

(if (fs/exists? dir)
  (assert (fs/directory? dir) (str "The given dir `" dir "` is not a directory."))
  (fs/create-dirs dir))

;; A simple mime type utility from https://github.com/ring-clojure/ring/blob/master/ring-core/src/ring/util/mime_type.clj
(def ^{:doc "A map of file extensions to mime-types."}
  default-mime-types
  {"7z"       "application/x-7z-compressed"
   "aac"      "audio/aac"
   "ai"       "application/postscript"
   "appcache" "text/cache-manifest"
   "asc"      "text/plain"
   "atom"     "application/atom+xml"
   "avi"      "video/x-msvideo"
   "bin"      "application/octet-stream"
   "bmp"      "image/bmp"
   "bz2"      "application/x-bzip"
   "class"    "application/octet-stream"
   "cer"      "application/pkix-cert"
   "conf"     "text/plain"
   "crl"      "application/pkix-crl"
   "crt"      "application/x-x509-ca-cert"
   "css"      "text/css"
   "csv"      "text/csv"
   "deb"      "application/x-deb"
   "dart"     "application/dart"
   "dll"      "application/octet-stream"
   "dmg"      "application/octet-stream"
   "dms"      "application/octet-stream"
   "doc"      "application/msword"
   "dvi"      "application/x-dvi"
   "edn"      "application/edn"
   "eot"      "application/vnd.ms-fontobject"
   "eps"      "application/postscript"
   "etx"      "text/x-setext"
   "exe"      "application/octet-stream"
   "flv"      "video/x-flv"
   "flac"     "audio/flac"
   "gif"      "image/gif"
   "gz"       "application/gzip"
   "htm"      "text/html"
   "html"     "text/html"
   "ico"      "image/x-icon"
   "iso"      "application/x-iso9660-image"
   "jar"      "application/java-archive"
   "jpe"      "image/jpeg"
   "jpeg"     "image/jpeg"
   "jpg"      "image/jpeg"
   "js"       "text/javascript"
   "json"     "application/json"
   "lha"      "application/octet-stream"
   "lzh"      "application/octet-stream"
   "mov"      "video/quicktime"
   "m3u8"     "application/x-mpegurl"
   "m4v"      "video/mp4"
   "mjs"      "text/javascript"
   "mp3"      "audio/mpeg"
   "mp4"      "video/mp4"
   "mpd"      "application/dash+xml"
   "mpe"      "video/mpeg"
   "mpeg"     "video/mpeg"
   "mpg"      "video/mpeg"
   "oga"      "audio/ogg"
   "ogg"      "audio/ogg"
   "ogv"      "video/ogg"
   "pbm"      "image/x-portable-bitmap"
   "pdf"      "application/pdf"
   "pgm"      "image/x-portable-graymap"
   "png"      "image/png"
   "pnm"      "image/x-portable-anymap"
   "ppm"      "image/x-portable-pixmap"
   "ppt"      "application/vnd.ms-powerpoint"
   "ps"       "application/postscript"
   "qt"       "video/quicktime"
   "rar"      "application/x-rar-compressed"
   "ras"      "image/x-cmu-raster"
   "rb"       "text/plain"
   "rd"       "text/plain"
   "rss"      "application/rss+xml"
   "rtf"      "application/rtf"
   "sgm"      "text/sgml"
   "sgml"     "text/sgml"
   "svg"      "image/svg+xml"
   "swf"      "application/x-shockwave-flash"
   "tar"      "application/x-tar"
   "tif"      "image/tiff"
   "tiff"     "image/tiff"
   "ts"       "video/mp2t"
   "ttf"      "font/ttf"
   "txt"      "text/plain"
   "webm"     "video/webm"
   "wmv"      "video/x-ms-wmv"
   "woff"     "font/woff"
   "woff2"    "font/woff2"
   "xbm"      "image/x-xbitmap"
   "xls"      "application/vnd.ms-excel"
   "xml"      "text/xml"
   "xpm"      "image/x-xpixmap"
   "xwd"      "image/x-xwindowdump"
   "zip"      "application/zip"})

;; https://github.com/ring-clojure/ring/blob/master/ring-core/src/ring/util/mime_type.clj
(defn- filename-ext
  "Returns the file extension of a filename or filepath."
  [filename]
  (when-let [ext (second (re-find #"\.([^./\\]+)$" filename))]
    (str/lower-case ext)))

;; https://github.com/ring-clojure/ring/blob/master/ring-core/src/ring/util/mime_type.clj
(defn ext-mime-type
  "Get the mimetype from the filename extension. Takes an optional map of
  extensions to mimetypes that overrides values in the default-mime-types map."
  ([filename]
   (ext-mime-type filename {}))
  ([filename mime-types]
   (let [mime-types (merge default-mime-types mime-types)]
     (mime-types (filename-ext filename)))))

(defn index [f]
  (let [files (map #(str (.relativize dir %)) (fs/list-dir f))]
    {:body
     (-> [:html
          [:head
           [:meta {:charset "UTF-8"}]
           [:title (str "Index of `" (.relativize dir f) "`")]]
          [:body
           [:h1 "Index of " [:code (str f)]]
           [:ul
            (for [child files]
              [:li [:a {:href (URLEncoder/encode (str child))} child
                    (when (fs/directory? (fs/path dir child)) "/")]])]
           [:hr]
           [:footer {:style {"text-aling" "center"}} "Served by http-server.clj"]]]
         html/html
         str)}))

(defn body [path]
  {:headers {"Content-Type" (ext-mime-type (fs/file-name path))}
   :body    (fs/file path)})

(defn start-server!
  []
  (server/run-server
   (fn [{:keys [:uri]}]
     (let [f          (fs/path dir (str/replace-first (URLDecoder/decode uri) #"^/" ""))
           index-file (fs/path f "index.html")]
       (cond
         (and (fs/directory? f) (fs/readable? index-file))
         (body index-file)

         (fs/directory? f)
         (index f)

         (fs/readable? f)
         (body f)

         :else
         {:status 404 :body (str "Not found `" f "` in " dir)})))
   {:port port}))

(defn -main
  [& _args]
  (start-server!)
  (loop []
    (Thread/sleep 3600)
    (recur)))

(-main)
