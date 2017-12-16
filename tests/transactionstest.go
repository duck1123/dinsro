package tests

import "github.com/revel/revel/testing"

type TransactionsTest struct {
	testing.TestSuite
}

func (t *TransactionsTest) Before() {
	println("Before Transactions Test")
}

func (t *TransactionsTest) TestTransactionsIndex() {
	t.Get("/transactions")
	t.AssertOk()
	t.AssertContentType("text/html; charset=utf-8")
}
