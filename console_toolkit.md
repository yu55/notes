# Regex notes
* http://regexr.com/
* Vim
  * `:%s/\s*\d\+m\s\d\+s//g` - delete entries like:' 1m 1s', ' 1m 10s', ' 10m 10s'
  * `:g/^$/d` - delete empty lines
  * `:%s/\./_/g`- replace dot '.' with underscore ; dot must be escaped
* commands
  * `find . -type d -empty -delete` - delete empty directories in this tree
  * `sed 'N;s/\n//'` delete every second newline
