package simple

import (
	"errors"
	"fmt"
	"strconv"
)

// constant
const (
	EMPTY = ""
)

func intDeclare(script string) {
	tokens := tokenize(script)
	node, err := intDeclareTokens(tokens)
	if err == nil {
		dumpast(node, EMPTY)
	}
}

func intDeclareTokens(tokens TokenReader) (node ASTNode, err error) {
	token := tokens.peek()
	if token == nil || token.GetTokenType() != IDINT3 {
		return nil, errors.New("key words error.")
	}
	tokens.read() // 消耗 int 关键字
	token = tokens.peek()
	if token == nil || token.GetTokenType() != ID {
		return nil, errors.New("variable name excepted.")
	}
	token = tokens.read() // 消耗 标识符
	result := &SimpleASTNode{
		nodeType: Identifier,
		nodeText: token.GetText(),
	}
	token = tokens.peek()
	if token != nil && token.GetTokenType() == Assignment {
		token = tokens.read() // 消耗 等号
		child := &SimpleASTNode{
			nodeType: Assignment,
			nodeText: token.GetText(),
		}
		child1, err := additive(tokens)
		if err != nil {
			return nil, err
		}
		if child1 == nil {
			return nil, errors.New("invalide variable initialization,excepting an expression.")
		}
		child.addChildren(child1)
		result.addChildren(child)
	}

	return result, nil
}

func evaluate(script string) {
	tree := parse(script)
	fmt.Println("token解析完成，进行打印。")
	dumpast(tree, EMPTY)
	fmt.Println("打印完毕，进行计算。")
	calculator(tree, EMPTY)
}

func calculator(node ASTNode, indent string) (num int, err error) {
	result := 0
	fmt.Println(indent + "Calculatoring：" + node.getType())
	switch node.getType() {
	case Programm:
		child := node.getChildren()
		for _, c := range child {
			res, err := calculator(c, indent+"\t")
			if err != nil {
				return 0, err
			}
			result += res
		}
	case Additive:
		child0 := node.getChildren()[0]
		valule0 := 0
		if res, err := calculator(child0, indent+"\t"); err == nil {
			valule0 = res
		} else {
			return 0, err
		}
		child1 := node.getChildren()[1]
		valule1 := 0
		if res, err := calculator(child1, indent+"\t"); err == nil {
			valule1 = res
		} else {
			return 0, err
		}
		if node.getText() == "+" {
			result = valule0 + valule1
		} else {
			result = valule0 - valule1
		}
	case Multiplicative:
		child0 := node.getChildren()[0]
		valule0 := 0
		if res, err := calculator(child0, indent+"\t"); err == nil {
			valule0 = res
		} else {
			return 0, err
		}
		child1 := node.getChildren()[1]
		valule1 := 0
		if res, err := calculator(child1, indent+"\t"); err == nil {
			valule1 = res
		} else {
			return 0, err
		}
		if node.getText() == "*" {
			result = valule0 * valule1
		} else {
			result = valule0 / valule1
		}
	case INTLITERAL:
		if res, err := strconv.Atoi(node.getText()); err == nil {
			result = res
		} else {
			return 0, err
		}
	}
	fmt.Printf("%s Result：%d. \n", indent, result)
	return result, nil
}

func parse(script string) ASTNode {
	tokens := tokenize(script)
	tree := prog(tokens)
	return tree
}

func prog(tokens TokenReader) ASTNode {
	astnode := &SimpleASTNode{
		nodeType: Programm,
		nodeText: "Calculator",
	}

	// if children, err := additive(tokens); err == nil {
	if children, err := additiveNew(tokens); err == nil {
		astnode.addChildren(children)
	}
	return astnode
}

// additive 加法表达式
/*
 * 新的语法规则
	采用 EBNF 方式表达
	add  ->  mul (+ mul)*
*/
func additiveNew(tokens TokenReader) (s *SimpleASTNode, err error) {
	child1, err := multiplicative(tokens)
	if err != nil {
		return nil, err
	}
	node := child1
	for {
		token := tokens.peek()
		if token == nil || (token.GetTokenType() != Plus && token.GetTokenType() != Minus) {
			break
		}
		token = tokens.read()
		child2, err := multiplicative(tokens)
		if err != nil {
			return nil, err
		}
		node = &SimpleASTNode{
			nodeType: Additive,
			nodeText: token.GetText(),
		}
		node.addChildren(child1)
		node.addChildren(child2)
		child1 = node
	}
	return node, nil
}

/*
 * 语法规则：
	add  ->  mul  |  add + mul
	mul  ->  pri  |  mul * pri
	pri  ->  id   |  Num  |  (add)
	会出先综合性问题
*/
func additive(tokens TokenReader) (s *SimpleASTNode, err error) {
	child1, err := multiplicative(tokens)
	if err != nil {
		return nil, err
	}
	node := child1
	token := tokens.peek()
	if child1 != nil && token != nil {
		if token.GetTokenType() == Plus || token.GetTokenType() == Minus {
			token = tokens.read()
			child2, err := additive(tokens)
			if err != nil {
				return nil, err
			}
			if child2 == nil {
				return nil, errors.New("invalid additive expression, excepting the right part.")
			}
			node = &SimpleASTNode{
				nodeType: Additive,
				nodeText: token.GetText(),
			}
			node.addChildren(child1)
			node.addChildren(child2)

		}
	}
	return node, nil
}

func multiplicative(tokens TokenReader) (s *SimpleASTNode, err error) {
	child1, err := primary(tokens)
	if err != nil {
		return nil, err
	}
	node := child1
	token := tokens.peek()
	if token != nil {
		if token.GetTokenType() == Star || token.GetTokenType() == Slash {
			token = tokens.read()
			child2, err := primary(tokens)
			if err != nil {
				return nil, err
			}
			if child2 == nil {
				return nil, errors.New("invalid multiplicative expression, excepting the right part.")
			}
			node = &SimpleASTNode{
				nodeText: token.GetText(),
				nodeType: Multiplicative,
			}
			node.addChildren(child1)
			node.addChildren(child2)
		}
	}
	return node, nil
}

func primary(tokens TokenReader) (s *SimpleASTNode, err error) {
	token := tokens.peek()
	if token != nil {
		if token.GetTokenType() == INTLITERAL {
			token = tokens.read()
			return &SimpleASTNode{
				nodeType: INTLITERAL,
				nodeText: token.GetText(),
			}, nil
		} else if token.GetTokenType() == Identifier {
			token = tokens.read()
			return &SimpleASTNode{
				nodeType: Identifier,
				nodeText: token.GetText(),
			}, nil
		} else if token.GetTokenType() == LeftParen {
			tokens.read()
			node, err := additive(tokens)
			if err != nil {
				return nil, err
			}
			if node == nil {
				return nil, errors.New("excepting an additive expression inside parenthesis.")
			}
			token = tokens.peek()
			if token == nil || token.GetTokenType() != RightParen {
				return nil, errors.New("excepting right parenthesis")
			}
			tokens.read()
			return node, nil
		}
	}
	return nil, nil
}

// type
const (
	Programm       = "Programm"       //程序入口，根节点
	IntDeclaration = "IntDeclaration" //整型变量声明
	ExpressionStmt = "ExpressionStmt" //表达式语句，即表达式后面跟个分号
	AssignmentStmt = "AssignmentStmt" //赋值语句
	Primary        = "Primary"        //基础表达式
	Multiplicative = "Multiplicative" //乘法表达式
	Additive       = "Additive"       //加法表达式
	Identifier     = "Identifier"     //标识符
)

func dumpast(node ASTNode, indent string) {
	fmt.Println(indent + node.getType() + " " + node.getText())
	children := node.getChildren()
	for _, v := range children {
		dumpast(v, indent+"\t")
	}
}

// SimpleASTNode ...
type SimpleASTNode struct {
	parent   *SimpleASTNode
	children []ASTNode
	nodeType string
	nodeText string
}

func (s *SimpleASTNode) addChildren(child *SimpleASTNode) {
	s.children = append(s.children, child)
	child.parent = s
}

func (s *SimpleASTNode) getParent() ASTNode {
	return s.parent
}

func (s *SimpleASTNode) getChildren() []ASTNode {
	return s.children
}

func (s *SimpleASTNode) getType() string {
	return s.nodeType
}
func (s *SimpleASTNode) getText() string {
	return s.nodeText
}

// ASTNode ...
type ASTNode interface {
	getParent() ASTNode
	getChildren() []ASTNode
	getType() string
	getText() string
}
