package simple

import "testing"

func Test_intDeclare(t *testing.T) {
	script := "int age = 40"
	intDeclare(script)
}

func Test_evaluate(t *testing.T) {
	script := "2 + 3 * 5"
	evaluate(script)
}

func Test_evaluate2(t *testing.T) {
	script := "2 + 3 + 5 + 4"
	evaluate(script)
}
