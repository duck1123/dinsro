package controllers

import (
	"github.com/revel/revel"
	"github.com/revel/revel/testing"
)

type UsersTest struct {
	testing.TestSuite
}

func (t *UsersTest) Before() {
	revel.AppLog.Info("Set up users test")
}

func (t *UsersTest) TestIndexAccounts() {
	t.Get("/api/v1/users/1/accounts")
	//contentType := t.Response.Header.Get()
	//t.AssertHeader("Content-Type", "app")
	t.AssertOk()
	t.AssertContains("foofsdfsfds")
	t.AssertContentType("application/json; charset=utf-8")
}

