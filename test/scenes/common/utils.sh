function get_container() {
  local service=$1
  echo "$ps" | awk -v service=$service -e '{ if(index($1, sprintf("_%s_", service)) > 1) { print $1; } }'
}

function get_port() {
  local service=$1
  local port=$2
  echo "$ps" | awk -v service=$service -v port=$port -e '{ if(index($0, sprintf("_%s_", service)) > 1) { match($0, sprintf(":([0-9]+)->%d/", port), a);  print a[1]; } }'
}
