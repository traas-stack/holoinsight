alias g="cd /home/admin/logs/holoinsight-server"

if [ -e "/home/admin/api" ] && [ ! -e "/home/admin/logs/api" ]; then
  ln -sf /home/admin/api /home/admin/logs/api || true
fi
