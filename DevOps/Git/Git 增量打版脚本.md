### patch.sh

```sh
# 定义sed -i 参数(数组)
# Default case for Linux sed, just use "-i"
sedi=(-i)
case "$(uname)" in
  # For macOS, use two parameters
  Darwin*) sedi=(-i "")
esac

function getTargetpathLog(){
  if [ -f targetpath.log ];then
  echo "存在targetpath,将删除该文件!";
  rm -rf targetpath.log
  fi;
  #取最新两条tag写入gitversion.log
  # git checkout origin/uat
  # git pull origin yat
  #git fetch --tags origin
  rm -rf git.log
  echo "请输入new分支名称(quit退出):"
  while true
  do
    read newbaseline
    newbaseline=$(eval echo $newbaseline)
    if [ $newbaseline = "quit" ];then
      exit 0;
    fi
    dev=`git branch | grep ".* ${newbaseline}$"`
    if [ -n "$dev" ];then
      break;
    else
      echo "当前工作区下此new分支不存在,请重新输入: "
    fi
  done
  echo "请输入old分支名称(quit退出): "
  while true
  do
    read oldbaseline
    if [ $oldbaseline = "quit" ];then
      exit 0;
    fi
    oldbaseline=$(eval echo $oldbaseline)
    feat=`git branch | grep ".* ${oldbaseline}$"`
    if [ -n "$feat" ];then
      break;
    else
      echo "当前工作区下此old分支不存在，请重新输入: "
    fi
  done
  git diff --name-status $oldbaseline $newbaseline > targetpath.log
}

function judgeSpeciel(){
  str="TaskWorkListServlet.java"
  res=$(echo ${full_path}| grep "${str}")
  if [ -n "$res" ]
  then
    #TODO
    tem=${full_path/\/src\/main\/java\/com\/hylandtec\/intelliplatform\/webapp/}
    target_path=${targetDir}/${tem%/*}
    copyFile
    return 1
  fi
}


function copyFile(){
 if [ "${source_path}" == "./" ]
 then
   return
 fi
 if [ -f "${source_path}" ]
 then
   if [ ! -d "${target_path}" ]
   then
     mkdir -p ${target_path}
   fi
 fi
 echo "复制[${source_path}]到[${target_path}]"
 cp ${source_path} ${target_path}
}

function copyCommon (){
  type1="webapp"
  type2="resources"
  type3=".java"
  type4="database"
  res1=$(echo ${full_path} | grep "${type1}")
  res2=$(echo ${full_path} | grep "${type2}")
  res3=$(echo ${full_path} | grep "${type3}")
  res4=$(echo ${full_path}| grep "${type4}")
  source_path= ${source}/${full_path}
  echo "源路径:${source_path}"
  if [ -n "$res1" ]
  then
    #TODO
    var= ${full_path/\/src\/main\/webapp/}
    target_path= ${targetDir}/${var%/*}
  elif [ -n "$res2" ]
  then
    #TODO
    if [ $project_name == "tfb-web"]
    then
      var=${full_path/\/src\/main\/resources\/WEB-INF\/classes}
      target_path= ${targetDir}/${var%/*}
      copyFile
      return
    fi
    var=${full_path/\/src\/main\/resources/}
    target_path=${targetDir}/${var%/*}
  elif [ -n "$res3" ]
  then
    #TODO
    judgeSpeciel
    flag=$?
    if [ $flag == 1 ]
    then
      return
    fi
    var=${full_path/\.java/\.class}
    var=${var/src\/main\/java/target\/classes}
    source_path=${source}/${var}
    if [ $project_name != "tfb-web" ]
    then
      var1=${var/\/target\/classes/}
      target_path=${targetDir}/${var1%/*}
      copyFile
      s=${var%/*}
      source_path="${source}/${s}""\$*.class"
      copyFile
      return
    fi
    var1=${var/target/WEB-INF}
    target_path=${targetDir}/${var1%/*}
    copyFile
    s=${var%.*}
    source_path="${source}/${s}""\$*.class"
  elif [ -n "$res4"]
  then
    #TODO
    echo "====${res4}======"
    source_path=${source}/${full_path}
    target_path=${targetDir}/${full_path%/*}
  fi
  copyFile
}

function getBatchzip (){
  echo "INFO:dealing batch"
  ls;
  rm -rf tfbbatch;
  mkdir tfbbatch;

  cp -r ${filePath}/* tfbbatch/
  cd tfbbatch
  #delete web in patch.txt
  sed "${sedi[@]}" '/tfb-web,/d' patch.txt;
  #delete dir
  rm -rf database
  rm -rf tfb-web
  if [[ $(ls | wc -l) -eq 1 ]];then
    echo "INFO:There is not other files but patch.txt,exit";
    cd ..
    rm -rf tfbbatch
    result=-1;
    return 1
  fi
  #move the batch.zip
  zip -r tfbbatch.zip *
  mv tfbbatch.zip ../.
  cd ..
  rm -rf tfbbatch
  echo "INFO;tfbbatch.zip success!"
  result=0;
}


function getWebZip(){
   echo "INFO:dealing web"
   ls;
   rm -rf tfbweb;
   mkdir tfbweb;
   cp -r ${filePath}/* tfbweb/

   cd tfbweb
   #delete batch in patch.txt
   #delete all other files
   sed "${sedi[@]}" '/hytfb-batch,/d' patch.txt;
   rm -rf database
   rm -rf hytfb-batch
   if [[ $(ls | wc -l) -eq 1 ]];then
      echo "ERROR:There is not files but patch.txt,exit"
      cd ..
      rm -rf tfbweb
      result=-1
      return 1;
   fi
   #仅生成打包一个web
   zip -r tfbweb.zip *
   mv tfbweb.zip ../.
   cd ..
   rm -rf tfbweb
   echo "INFO;tfb-web.zip auccess!"
   result=0;
}


main(){
  getTargetpathLog
  source=.
  target=./tfbsit
  date=` date +%Y%m%d`
  filePath="tfb_uat"
  targetDir=${target}/${date}/${filePath}
  if [ -d "${targetDir}" ]
  then
    rm -r ${targetDir}
  fi

  #读取targetpach.log
  while read line
  do
    info=($line)
    full_path=${info[1]}
    #echo $full_path
    project_name=${full_path%%/*}
    echo "======${project_name}========="
    copyCommon
  done < targetpath.log

  cd ${target}/${date}
  pwd
  #生成patch.txt文件
  find ${filePath} | xargs ls -ld | grep -v ^d |grep -v '.*patch,txt' | grep -Ev '*\.sh'| grep -Ev '*\.sql'|grep -Ev '*\.tar'> ${filePath}/patch.txt
  sed "${sedi[@]}" 's/.*'${filePath}'\//r,/g' ${filePath}/patch.txt
  sed "${sedi[@]}" 's/\//,/' ${filePath}/patch.txt
  #生成zip文件
  #run batch
  if [[ $(ls ${filePath} | grep -v tfb-web | wc -l) -gt 0 ]];then
    getBatchzip;
  fi
  #run web
  if [[ $(ls ${filePath}| grep -v hytfb-batch | wc -l) -gt 0 ]];then
    getWebZip;
  fi
}

main
```

使用

```sh
./patch.sh
```

new 分支一般使用 UAT, 分支， old 分支一般是 feat 分支合并到 UAT 分支后打了一个 tag, 这个 tag 就是 old 分支。结合 Git 实战指南博客使用更佳哦。