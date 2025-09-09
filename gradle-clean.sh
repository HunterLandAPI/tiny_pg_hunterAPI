#!/bin/bash
# =======================================================
# Gradle 완전 정리 및 재설정 스크립트
# =======================================================

echo "🧹 Gradle 완전 정리를 시작합니다..."

# 색상 설정
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}🔄 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_step "Gradle 데몬 정지 중..."
./gradlew --stop
print_success "Gradle 데몬 정지 완료"

print_step "전역 Gradle 캐시 정리 중..."
rm -rf ~/.gradle/caches
rm -rf ~/.gradle/daemon
rm -rf ~/.gradle/wrapper
print_success "전역 캐시 정리 완료"

print_step "프로젝트 캐시 정리 중..."
rm -rf .gradle
rm -rf build
print_success "프로젝트 캐시 정리 완료"

print_step "Gradle Wrapper 재설정 중..."
./gradlew wrapper --gradle-version 8.14
print_success "Gradle Wrapper 재설정 완료"

print_step "권한 설정 중..."
chmod +x gradlew
print_success "권한 설정 완료"

print_step "의존성 다운로드 중..."
./gradlew --refresh-dependencies
print_success "의존성 다운로드 완료"

echo ""
echo "🎉 Gradle 정리가 완료되었습니다!"
echo ""
echo "다음 명령어로 빌드를 시도해보세요:"
echo "  ./gradlew clean build"
echo "  ./gradlew shadowJar"
echo "  ./gradlew runServer"
