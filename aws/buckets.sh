#!/usr/bin/env bash

# S3 서비스가 준비될 때까지 기다리기
until awslocal s3 ls > /dev/null 2>&1; do
  echo "Waiting for S3 service to be ready..."
  sleep 2
done

# 버킷 생성
awslocal s3 mb s3://botanify-backend-bucket