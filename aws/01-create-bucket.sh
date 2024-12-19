#!/usr/bin/env sh

# docker-compose 설정에 의해, localstack 이미지의 시작과 함께 실행되는 스크립트.
# 지정된 명칭의 버킷을 생성한다.

# S3 서비스가 준비될 때까지 기다리기
until awslocal s3 ls > /dev/null 2>&1; do
  echo "Waiting for S3 service to be ready..."
  sleep 2
done

# 버킷 생성
awslocal s3 mb s3://botanify-backend-bucket