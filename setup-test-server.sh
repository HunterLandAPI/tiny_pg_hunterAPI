#!/bin/bash
# =======================================================
# 마인크래프트 플러그인 테스트 서버 설정 스크립트
# =======================================================

echo "🚀 마인크래프트 플러그인 테스트 서버를 설정합니다..."

# 색상 설정
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_step() {
    echo -e "${BLUE}🔄 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}📋 $1${NC}"
}

# 작업 디렉토리 설정
TEST_SERVER_DIR="test-server"
PLUGINS_DIR="$TEST_SERVER_DIR/plugins"

# 디렉토리 생성
print_step "테스트 서버 디렉토리 설정 중..."
mkdir -p "$PLUGINS_DIR"
cd "$TEST_SERVER_DIR"

# EULA 자동 동의
print_step "EULA 설정 중..."
echo "eula=true" > eula.txt
print_success "EULA 동의 완료"

# 서버 설정 파일 생성
print_step "서버 설정 파일 생성 중..."
cat > server.properties << EOF
# 마인크래프트 테스트 서버 설정
server-port=25565
gamemode=creative
difficulty=peaceful
max-players=5
online-mode=false
white-list=false
enable-command-block=true
spawn-protection=0
motd=Tiny PG Hunter API Test Server
level-name=world
level-type=flat
generate-structures=false
spawn-monsters=false
spawn-animals=false
spawn-npcs=false
EOF
print_success "서버 설정 완료"

# 플러그인 복사
print_step "플러그인 복사 중..."
if [ -f "../build/libs/tiny_pg_hunterAPI-1.0-SNAPSHOT.jar" ]; then
    cp "../build/libs/tiny_pg_hunterAPI-1.0-SNAPSHOT.jar" "plugins/"
    print_success "플러그인 복사 완료"
else
    echo "❌ 플러그인 JAR 파일을 찾을 수 없습니다."
    echo "   먼저 ./gradlew shadowJar 를 실행하세요."
    exit 1
fi

# Paper 서버 확인
print_step "Paper 서버 확인 중..."
if [ ! -f "paper-1.20.6.jar" ]; then
    print_step "Paper 서버 다운로드 중..."
    curl -o paper-1.20.6.jar "https://api.papermc.io/v2/projects/paper/versions/1.20.6/builds/147/downloads/paper-1.20.6-147.jar"
    print_success "Paper 서버 다운로드 완료"
else
    print_success "Paper 서버 파일 확인됨"
fi

# 시작 스크립트 생성
print_step "시작 스크립트 생성 중..."
cat > start-server.sh << 'EOF'
#!/bin/bash
echo "🎮 마인크래프트 테스트 서버를 시작합니다..."
echo ""
echo "📋 서버 명령어들:"
echo "  plugins           # 플러그인 목록"
echo "  hello             # 플러그인 테스트"
echo "  version           # 서버 버전"
echo "  tps               # 서버 성능"
echo "  stop              # 서버 중지"
echo ""
echo "🔧 백스페이스, 화살표 키 등이 정상 작동합니다!"
echo ""

java -Xmx2G -Xms1G -jar paper-1.20.6.jar
EOF

chmod +x start-server.sh
print_success "시작 스크립트 생성 완료"

echo ""
echo "🎉 테스트 서버 설정이 완료되었습니다!"
echo ""
print_info "서버 시작 방법:"
echo "  cd test-server"
echo "  ./start-server.sh"
echo ""
print_info "또는 직접 실행:"
echo "  cd test-server"
echo "  java -jar paper-1.20.6.jar"
echo ""
print_info "마인크래프트 클라이언트 접속:"
echo "  서버 주소: localhost"
echo "  버전: 1.20.6"
