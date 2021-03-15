function getlist_device_id(){
  arr=()
  adb devices | grep emulator | cut -f1 | while read line; do arr+=($line) ; done
}

function wait_emulator_to_be_ready() {
  $(getlist_device_id)
  for i in "${arr[@]}"; do adb -s $i emu kill; done
  count = 0
  while [ $a -lt $no_Emulator ]; do
    emulator -avd test -no-audio -no-boot-anim -no-window -accel on -gpu off -skin 1440x2880 &
    a=`expr $count + 1`
    sleep 1
  done
  $(getlist_device_id)
  boot_completed=false
  while [ "$boot_completed" == false ]; do
    status=$(adb wait-for-device shell getprop sys.boot_completed | tr -d '\r')
    echo "Boot Status: $status"

    if [ "$status" == "1" ]; then
      boot_completed=true
    else
      sleep 1
    fi
  done
}

function disable_animation() {
  for i in "${arr[@]}"; do 
    adb -s $i shell "settings put global window_animation_scale 0.0"
    adb -s $i shell "settings put global transition_animation_scale 0.0"
    adb -s $i shell "settings put global animator_duration_scale 0.0"
    echo $i
  done
}

no_Emulator=$1
if [[ -n "$1" ]]; then
  no_Emulator=$1  
else
  no_Emulator=1
fi

wait_emulator_to_be_ready
sleep 1
disable_animation