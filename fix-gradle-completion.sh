#!/bin/bash
# =======================================================
# Gradle 자동완성 문제 해결 스크립트
# =======================================================

echo "🔧 Gradle 자동완성 문제를 해결합니다..."

# 현재 셸 확인
if [[ $SHELL == *"zsh"* ]]; then
    echo "✅ zsh 셸 감지됨"
    
    # 1. 임시 해결 (현재 세션)
    echo "📋 현재 세션에서 gradle completion 비활성화..."
    compdef -d gradle 2>/dev/null
    
    # 2. 영구 해결책을 ~/.zshrc에 추가할지 묻기
    echo ""
    read -p "🤔 ~/.zshrc에 영구 설정을 추가하시겠습니까? (y/n): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        # 기존 설정이 있는지 확인
        if ! grep -q "# Gradle completion 비활성화" ~/.zshrc; then
            echo "" >> ~/.zshrc
            echo "# Gradle completion 비활성화 (gradlew 자동완성 오류 방지)" >> ~/.zshrc
            echo "compdef -d gradle 2>/dev/null" >> ~/.zshrc
            echo "✅ ~/.zshrc에 설정 추가 완료"
        else
            echo "ℹ️  이미 설정이 존재합니다."
        fi
        
        # 설정 재로드
        source ~/.zshrc
        echo "🔄 zsh 설정 재로드 완료"
    fi
    
elif [[ $SHELL == *"bash"* ]]; then
    echo "✅ bash 셸 감지됨"
    echo "ℹ️  bash에서는 보통 이 문제가 발생하지 않습니다."
else
    echo "⚠️  알 수 없는 셸: $SHELL"
fi

echo ""
echo "🎯 이제 다음 명령어들을 테스트해보세요:"
echo "   ./gradlew clean        # Tab 키 사용하지 않고"
echo "   ./gradlew build        # 직접 타이핑"
echo "   ./gradlew shadowJar    # 또는 복사-붙여넣기"
echo ""
echo "💡 Tip: 자주 사용하는 명령어는 별칭을 만들어보세요:"
echo "   alias gb='./gradlew build'"
echo "   alias gs='./gradlew shadowJar'"
echo "   alias gr='./gradlew runServer'"
