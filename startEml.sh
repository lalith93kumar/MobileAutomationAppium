source ~/.bash_profile
declare -a arr
#! /bin/zsh
function getlist_device_id {
  arr=()
  output=$(adb devices | grep emulator | cut -f1)
  arr=$(echo $output | tr " " "\n")
  echo $arr
}

function stop_emulator {
  getlist_device_id
  for i in $arr
  do
    echo $i
    adb -s $i emu kill
  done 
}

function wait_emulator_to_be_ready {
  stop_emulator
  echo "lalith1"
  count=0
  while [ $count -lt $no_Emulator ]; do
    emulator -avd test -no-audio -no-window -accel on -gpu off -skin 1440x2880 -writable-system -wipe-data -read-only &
    count=`expr $count + 1`
    sleep 5s
  done
  getlist_device_id
  boot_completed=false
  while [ "$boot_completed" == false ]; do
    i=$(echo ${arr} | cut -d " " -f ${no_Emulator}-)
    echo "${i}"
    status=$(adb -s "$i" shell getprop sys.boot_completed | tr -d '\r')
    echo "Boot Status: $status"
    if [ "$status" == "1" ]; then
      boot_completed=true
    else
      sleep 10s
    fi
  done
}

function disable_animation {
  getlist_device_id
  for i in $arr
  do 
    echo $i
    adb -s "$i" root
    sleep 5s
    adb -s "$i" remount
    ca=~/Desktop/CertificateAuthorityCertificate.pem
    hash=$(openssl x509 -noout -subject_hash_old -in $ca)
    adb -s "$i" push $ca /system/etc/security/cacerts/$hash.0
    adb -s "$i" shell "settings put global window_animation_scale 0.0"
    adb -s "$i" shell "settings put global transition_animation_scale 0.0"
    adb -s "$i" shell "settings put global animator_duration_scale 0.0"
    echo $i
    echo "certification installed"
  done
}

no_Emulator=$1
if [[ -n "$1" ]]; then
  no_Emulator=$1  
else
  no_Emulator=1
fi
if [ -f ~/.bashrc ]; then
   source ~/.bashrc
fi
wait_emulator_to_be_ready
sleep 1
disable_animation