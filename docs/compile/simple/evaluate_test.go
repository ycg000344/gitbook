package simple

import "testing"

func Test_evaluate(t *testing.T) {
	script := "2 + 3 * 5"
	evaluate(script)
}
