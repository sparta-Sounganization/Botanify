#!/bin/bash

# docker-compose 설정에 의해, localstack 이미지의 시작과 함께 실행되는 스크립트.
# 버킷 생성 스크립트 동작 후에 동작하여야 함.

# LocalStack S3에 CORS 정책 적용
BUCKET_NAME="botanify-backend-bucket"
CORS_POLICY='{
  "CORSRules": [
    {
      "AllowedOrigins": ["https://example.com"],
      "AllowedMethods": ["GET"],
      "AllowedHeaders": ["*"]
    }
  ]
}'

awslocal s3api put-bucket-cors --bucket $BUCKET_NAME --cors-configuration "$CORS_POLICY"