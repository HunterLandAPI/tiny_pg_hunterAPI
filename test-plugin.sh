#!/bin/bash
# =======================================================
# Minecraft Plugin 테스트 스크립트
# =======================================================

echo "🎮 Minecraft Plugin 테스트를 시작합니다..."

# 서버가 실행 중인지 확인
if ! lsof -i :25565 > /dev/null 2>&1; then
    echo "❌ 마인크래프트 서버가 실행되지 않았습니다."
    echo "   먼저 ./gradlew runServer 를 실행하세요."
    exit 1
fi

echo "✅ 서버가 실행 중입니다."
echo ""
echo "다음 명령어들을 서버 콘솔에서 테스트해보세요:"
echo ""
echo "1️⃣  plugins           # 플러그인 목록 확인"
echo "2️⃣  hello             # 플러그인 명령어 테스트"  
echo "3️⃣  version           # 서버 버전 확인"
echo "4️⃣  tps               # 서버 성능 확인"
echo "5️⃣  help              # 도움말"
echo "6️⃣  stop              # 서버 종료"
echo ""
echo "🔧 플러그인 디버깅:"
echo "   - 플러그인이 목록에 없으면: 로딩 실패"
echo "   - 명령어가 작동하지 않으면: plugin.yml 확인"
echo "   - 데이터베이스 에러: MySQL 설정 확인"
echo ""
echo "📍 서버 콘솔은 runServer를 실행한 터미널에서 확인하세요!"
