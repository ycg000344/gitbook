package simple

import (
	"fmt"
	"strings"
	"unicode"
)

var tokens []Token
var token Token
var tokenText strings.Builder

func tokenize(script string) TokenReader {
	if strings.Count(script, "") == 0 {
		return nil
	}

	tokens = make([]Token, 0)
	token = new(SimpleToken)

	newSate := INITIAL
	runrs := []rune(script)
	var ch = rune(0)
	for _, ch := range runrs {
		switch newSate {
		case INITIAL:
			newSate = initToken(ch)
		case ID:
			if unicode.IsDigit(ch) || unicode.IsLetter(ch) {
				tokenText.WriteRune(ch)
			} else {
				newSate = initToken(ch)
			}
		case GT:
			if ch == '=' {
				newSate = GE
				token.SetTokenType(GE)
				tokenText.WriteRune(ch)
			} else {
				newSate = initToken(ch)
			}
		case INTLITERAL:
			if unicode.IsDigit(ch) {
				tokenText.WriteRune(ch)
			} else {
				newSate = initToken(ch)
			}
		case IDINT1:
			if ch == 'n' {
				newSate = IDINT2
				tokenText.WriteRune(ch)
			} else if unicode.IsDigit(ch) || unicode.IsLetter(ch) {
				newSate = ID
				tokenText.WriteRune(ch)
			} else {
				newSate = initToken(ch)
			}
		case IDINT2:
			if ch == 't' {
				newSate = IDINT3
				tokenText.WriteRune(ch)
			} else if unicode.IsDigit(ch) || unicode.IsLetter(ch) {
				newSate = ID
				tokenText.WriteRune(ch)
			} else {
				newSate = initToken(ch)
			}
		case IDINT3:
			if unicode.IsDigit(ch) || unicode.IsLetter(ch) {
				newSate = ID
				tokenText.WriteRune(ch)
			} else {
				token.SetTokenType(IDINT3)
				newSate = initToken(ch)
			}
		default:
			newSate = initToken(ch)
		}

	}
	if tokenText.Len() > 0 {
		initToken(ch)
	}
	reader := SimpleTokenReader{
		tokens:   tokens,
		position: 0,
	}
	return &reader
}

func initToken(ch rune) string {
	if tokenText.Len() > 0 {
		token.SetText(tokenText.String())
		tokens = append(tokens, token)

		tokenText.Reset()
		token = new(SimpleToken)
	}
	newSate := INITIAL
	if unicode.IsLetter(ch) {
		if ch == 'i' {
			newSate = IDINT1
		} else {
			newSate = ID
		}
		token.SetTokenType(ID)
		tokenText.WriteRune(ch)
	} else if unicode.IsDigit(ch) {
		newSate = INTLITERAL
		tokenText.WriteRune(ch)
		token.SetTokenType(INTLITERAL)
	} else if ch == '>' {
		newSate = GT
		tokenText.WriteRune(ch)
		token.SetTokenType(GT)
	} else if ch == '+' {
		newSate = Plus
		tokenText.WriteRune(ch)
		token.SetTokenType(Plus)
	} else if ch == '-' {
		newSate = Minus
		tokenText.WriteRune(ch)
		token.SetTokenType(Minus)
	} else if ch == '*' {
		newSate = Star
		tokenText.WriteRune(ch)
		token.SetTokenType(Star)
	} else if ch == '/' {
		newSate = Slash
		tokenText.WriteRune(ch)
		token.SetTokenType(Slash)
	} else if ch == '(' {
		newSate = LeftParen
		tokenText.WriteRune(ch)
		token.SetTokenType(LeftParen)
	} else if ch == ')' {
		newSate = RightParen
		tokenText.WriteRune(ch)
		token.SetTokenType(RightParen)
	} else if ch == '=' {
		newSate = Assignment
		tokenText.WriteRune(ch)
		token.SetTokenType(Assignment)
	} else if ch == ';' {
		newSate = SemiColon
		tokenText.WriteRune(ch)
		token.SetTokenType(SemiColon)
	} else {
		newSate = INITIAL
	}
	return newSate
}

func dump(s TokenReader) {
	fmt.Println("text \t type.")
	var token Token
	for {
		token = s.read()
		if token == nil {
			return
		}
		fmt.Printf("%s \t %s. \n", token.GetText(), token.GetTokenType())
	}

}

// tokenType
const (
	INITIAL    = "INITIAL"    // 初始化
	ID         = "ID"         // 标识符
	INTLITERAL = "INTLITERAL" // 数字
	GT         = "GT"         // 大于
	GE         = "GE"         // 大于等于
	Plus       = "Plus"       //  +
	Minus      = "Minus"      // -
	Star       = "Star"       // *
	Slash      = "Slash"      // /
	SemiColon  = "SemiColon"  // ；
	LeftParen  = "LeftParen"  // (
	RightParen = "RightParen" // )
	Assignment = "Assignment" // =
	IDINT1     = "I"          // i
	IDINT2     = "N"          // n
	IDINT3     = "INT"        // t
)

// Token ...
type Token interface {
	GetTokenType() string
	GetText() string
	SetTokenType(tokenType string)
	SetText(text string)
}

// SimpleToken ...
type SimpleToken struct {
	tokenType string
	text      string
}

// GetTokenType ...
func (s *SimpleToken) GetTokenType() string {
	return s.tokenType
}

// GetText ....
func (s *SimpleToken) GetText() string {
	return s.text
}

// SetTokenType ...
func (s *SimpleToken) SetTokenType(tokenType string) {
	s.tokenType = tokenType
}

// SetText ...
func (s *SimpleToken) SetText(text string) {
	s.text = text
}

// TokenReader ...
type TokenReader interface {
	read() Token
	peek() Token
	unread()
	getPosition() int
	setPosition(pos int)
}

type SimpleTokenReader struct {
	tokens   []Token
	position int
}

func (s *SimpleTokenReader) read() Token {
	if s.position >= len(s.tokens) {
		return nil
	}
	token := s.tokens[s.getPosition()]
	s.position = s.getPosition() + 1
	return token
}

func (s *SimpleTokenReader) peek() Token {
	if s.position >= len(s.tokens) {
		return nil
	}
	return s.tokens[s.position]
}

func (s *SimpleTokenReader) unread() {
	if s.position <= 0 {
		return
	}
	s.position--
}

func (s *SimpleTokenReader) getPosition() int {
	return s.position
}
func (s *SimpleTokenReader) setPosition(position int) {
	if position > 0 && position < len(s.tokens) {
		s.position = position
	}
}
