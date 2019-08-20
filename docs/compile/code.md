# 编译原理

> 极客时间，《编译原理之美》专栏，学习笔记
>
> https://time.geekbang.org/column/intro/219

## 正则文法和有限自动机

### 纯手工打造词法分析器

#### code

```java
public class SimpleLexer {

    // 临时保存 token 的文本
    private StringBuffer tokenText;
    // 保存解析出来的 token
    private List<Token> tokens;
    // 当前解析出来的 token
    private SimpleToken token;

    private String script;

    @Test
    public void state4(){
        script = "2+3*5";
    }

    @Test
    public void state3(){
        script  = "intA = 45";
    }

    @Test
    public void state2(){
        script  = "int age = 45";
    }

    /**
     *  解析
     *  age >= 45
     */
    @Test
    public void state1(){
         script = "age >= 45";
    }

    @After
    public void after(){
        SimpleTokenReader tokenize = this.tokenize(script);
        dump(tokenize);
    }

    private SimpleTokenReader tokenize(String script) {
        // TODO: 2019/8/19  state1 start
        tokens = new ArrayList<>();
        token = new SimpleToken();
        tokenText = new StringBuffer();

        CharArrayReader reader = new CharArrayReader(script.toCharArray());
        int ich;
        char ch = 0;
        
        DfaState state = DfaState.Initial;      // 初始化
        try {
            while ((ich = reader.read()) != -1) {
                ch = (char)ich;
                switch (state) {
                    case Initial:
                        state = initToken(ch);  // 重新确定后续状态
                        break;
                    case Id:                    // 标识符
                        if (this.isAlpha(ch) || this.isDigit(ch)) {
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case GT:                    // 比较运算符
                        if (ch == '=') {        // 切换状态
                            state = DfaState.GE;
                            token.type = TokenType.GE;
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case GE:
                        state = initToken(ch);  // 重新确定后续状态
                        break;
                    case IntLiteral:            // 数字字面量
                        if (this.isDigit(ch)) {
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_int1:               // 关键字 int 判断
                        if (ch == 'n') {
                            state = DfaState.Id_int2;
                            tokenText.append(ch);
                        } else if (this.isAlpha(ch) || this.isDigit(ch)) {
                            state = DfaState.Id;
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_int2:
                        if (ch == 't') {
                            state = DfaState.Id_int3;
                            tokenText.append(ch);
                        } else if (this.isDigit(ch) || this.isAlpha(ch)) {
                            state = DfaState.Id;
                            tokenText.append(ch);
                        } else {
                            state = initToken(ch);
                        }
                        break;
                    case Id_int3:
                        if (this.isBlank(ch)) {
                            token.type = TokenType.Int; // 关键字状态
                            state = initToken(ch);
                        } else {
                            state = DfaState.Id;
                            tokenText.append(ch);
                        }
                        break;
                     default:
                         state = initToken(ch);
                            break;

                }
            }
            // 把最后一个 token 装进去
            if (tokenText.length() > 0) {
                initToken(ch);
            }
        } catch (Exception e) {

        }

        return new SimpleTokenReader(tokens);
        // TODO: 2019/8/19 state1 end
    }



    private DfaState initToken(char ch) {
        if (tokenText.length() > 0) {
            token.text = tokenText.toString();
            tokens.add(token);

            tokenText = new StringBuffer();
            token = new SimpleToken();
        }

        DfaState newState;

        if (this.isAlpha(ch)) {                 // 第一个是 字母
            if (ch == 'i') {
                newState = DfaState.Id_int1;    // 关键字
            } else {
                newState = DfaState.Id;         // 标识符
            }
            token.type = TokenType.Identifier;
            tokenText.append(ch);
        } else if (this.isDigit(ch)) {          // 第一个是 数字
            newState = DfaState.IntLiteral;
            token.type = TokenType.IntLiteral;
            tokenText.append(ch);
        } else if (ch == '>') {
            newState = DfaState.GT;
            token.type = TokenType.GT;
            tokenText.append(ch);
        } else if (ch == '=') {
            newState = DfaState.Assignment;
            token.type = TokenType.Assignment;
            tokenText.append(ch);
        } else if (ch == '+') {
            newState = DfaState.Plus;
            token.type = TokenType.Plus;
            tokenText.append(ch);
        } else if (ch == '-') {
            newState = DfaState.Minus;
            token.type = TokenType.Minus;
            tokenText.append(ch);
        }else if (ch == '*') {
            newState = DfaState.Star;
            token.type = TokenType.Star;
            tokenText.append(ch);
        }else if (ch == '/') {
            newState = DfaState.Slash;
            token.type = TokenType.Slash;
            tokenText.append(ch);
        } else {
            newState = DfaState.Initial;
        }

        return newState;
    }

    private boolean isBlank(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n';
    }

    private boolean isDigit(int ch) {
        return ch >= '0' && ch <= '9';
    }

    private boolean isAlpha(int ch) {
        return ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'B';
    }
    private void dump(SimpleTokenReader tokenize) {
        Token token;
        while ((token = tokenize.read()) != null) {
            System.out.println(String.format("%s\t%s", token.getType(), token.getText()));
        }
    }

    class SimpleToken implements Token {
        private TokenType type;
        private String text;
        @Override
        public TokenType getType() {
            return type;
        }

        @Override
        public String getText() {
            return text;
        }
    }

    class SimpleTokenReader implements TokenReader {
        private List<Token> tokens;
        private int position;

        public SimpleTokenReader(List<Token> tokens) {
            this.tokens = tokens;

        }

        @Override
        public Token read() {
            if (position < tokens.size()) {
                return tokens.get(position++);
            }
            return null;
        }

        @Override
        public Token peek() {
            if (position < tokens.size()) {
                return tokens.get(position);
            }
            return null;
        }

        @Override
        public void unread() {
            if (position > 0) {
                position--;
            }
        }

        @Override
        public int getPosition() {
            return position;
        }

        @Override
        public void setPosition(int position) {
            if (position > 0 && position < tokens.size()) {
                this.position = position;
            }
        }
    }


    interface TokenReader{
        Token read();

        Token peek();

        void unread();

        int getPosition();

        void setPosition(int position);
    }

    interface Token{
        TokenType getType();
        String getText();
    }

    enum TokenType {
        Identifier,
        IntLiteral,
        GT,
        GE,
        Int,
        Assignment,
        Plus,
        Minus,
        Star,
        Slash,
    }
    enum DfaState{
        Initial,    // 初始化
        Id,         // 标识符号
        GT,         // 大于
        GE,         // 大于等于
        IntLiteral, // 数字
        Id_int1,    // i
        Id_int2,    // n
        Id_int3,    // t
        Assignment, // =
        Plus,       // +
        Minus,      // -
        Star,       // *
        Slash,      // /
    }
}

```

