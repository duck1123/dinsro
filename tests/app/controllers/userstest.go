package controllers

import (
	"github.com/revel/revel"
	"github.com/revel/revel/testing"
	"net/http"
)

type UsersTest struct {
	testing.TestSuite
}

func (t *UsersTest) Before() {
	revel.AppLog.Info("Set up users test")
}

func (t *UsersTest) TestIndexAccounts() {
	path := "/api/v1/users/1/accounts"
	req, err := http.NewRequest(http.MethodGet, path, nil)
	if err != nil {
		panic(err)
	}

	testRequest := t.NewTestRequest(req)
	revel.AppLog.Info(testRequest.Proto)
	//t.Get()
	//contentType := t.Response.Header.Get()
	//t.AssertHeader("Content-Type", "app")
	t.AssertOk()
	t.AssertContains("foofsdfsfds")
	t.AssertContentType("application/json; charset=utf-8")
}

