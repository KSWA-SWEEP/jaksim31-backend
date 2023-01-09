# 가동중인 awsstudy 도커 중단 및 삭제
#sudo docker ps -a -q --filter "name=formduo" | grep -q . && docker stop formduo && docker rm formduo | true

sudo docker-compose stop

sudo docker rm redis

sudo docker rm formduo-spring

# 기존 이미지 삭제
sudo docker rmi qkdrmsgh73/formduo

# 도커 실행
sudo docker-compose up -d

## 도커허브 이미지 pull
#sudo docker pull qkdrmsgh73/formduo

## 도커 run
#docker run -d -p 8082:8080 --name formduo qkdrmsgh73/formduo

# 사용하지 않는 불필요한 이미지 삭제 -> 현재 컨테이너가 물고 있는 이미지는 삭제되지 않습니다.
sudo docker rmi -f $(docker images -f "dangling=true" -q) || true
