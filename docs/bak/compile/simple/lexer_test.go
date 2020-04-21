package simple

import "testing"

func Test_lexer(t *testing.T) {
	script := "int age = 40"
	reader := tokenize(script)
	dump(reader)
}
