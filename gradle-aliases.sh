# Gradle 프로젝트용 편리한 별칭들
# ~/.zshrc 또는 ~/.bashrc에 추가하면 됩니다

# 기본 Gradle 명령어들
alias gb='./gradlew build'
alias gc='./gradlew clean'
alias gs='./gradlew shadowJar'
alias gr='./gradlew runServer'
alias gt='./gradlew test'

# 조합 명령어들  
alias gcb='./gradlew clean build'
alias gcs='./gradlew clean shadowJar'
alias gcr='./gradlew clean runServer'

# 정보 확인
alias gd='./gradlew dependencies'
alias gp='./gradlew projects'
alias gtasks='./gradlew tasks'

# 마인크래프트 플러그인 특화
alias plugin-build='./gradlew clean shadowJar'
alias plugin-test='./gradlew runServer'
alias plugin-setup='./setup-test-server.sh'

# 사용법 예시:
# gb        → ./gradlew build
# gcr       → ./gradlew clean runServer  
# plugin-build → ./gradlew clean shadowJar
