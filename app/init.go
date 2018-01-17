package app

import (
	"github.com/duck1123/dinsro/app/controllers"
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
	"github.com/revel/revel"
	"golang.org/x/crypto/bcrypt"
)

func init() {
	// Filters is the default set of global filters.
	revel.Filters = []revel.Filter{
		revel.PanicFilter,             // Recover from panics and display an error page instead.
		revel.RouterFilter,            // Use the routing table to select the right Action
		revel.FilterConfiguringFilter, // A hook for adding or removing per-Action filters.
		revel.ParamsFilter,            // Parse parameters into Controller.Params.
		revel.SessionFilter,           // Restore and write the session cookie.
		revel.FlashFilter,             // Restore and write the flash cookie.
		revel.ValidationFilter,        // Restore kept validation errors and save new ones from cookie.
		revel.I18nFilter,              // Resolve the requested language
		HeaderFilter,                  // Add some security based headers
		revel.InterceptorFilter,       // Run interceptors around the action.
		revel.CompressFilter,          // Compress the result.
		revel.ActionInvoker,           // Invoke the action.
	}

	// Register startup functions with OnAppStart
	// revel.DevMode and revel.RunMode only work inside of OnAppStart. See Example Startup Script
	// ( order dependent )
	// revel.OnAppStart(ExampleStartupScript)
	revel.OnAppStart(InitDB, 5)

	// revel.OnAppStart(InitDB)
	// revel.OnAppStart(FillCache)
}

// HeaderFilter adds common security headers
// There is a full implementation of a CSRF filter in
// https://github.com/revel/modules/tree/master/csrf
var HeaderFilter = func(c *revel.Controller, fc []revel.Filter) {
	c.Response.Out.Header().Add("X-Frame-Options", "SAMEORIGIN")
	c.Response.Out.Header().Add("X-XSS-Protection", "1; mode=block")
	c.Response.Out.Header().Add("X-Content-Type-Options", "nosniff")

	fc[0](c, fc[1:]) // Execute the next filter stage.
}

//func ExampleStartupScript() {
//	// revel.DevMod and revel.RunMode work here
//	// Use this script to check for dev mode and set dev/prod startup scripts here!
//	if revel.DevMode == true {
//		// Dev mode
//	}
//}

func InitDB() {
	gorp.Db.SetDbInit(func(db *gorp.DbGorp) error {
		dbmap := db.Map
		db.TraceOn(revel.AppLog)

		dbmap.AddTableWithName(models.Account{}, "accounts")
		dbmap.AddTableWithName(models.Transaction{}, "transactions")
		dbmap.AddTableWithName(models.User{}, "users")
		dbmap.CreateTables()

		var err error

		password := "perfectloops"
		bcryptPassword, _ := bcrypt.GenerateFromPassword([]byte(password), bcrypt.DefaultCost)

		admin := models.User{
			Name:         "admin",
			Email:        "admin@example.com",
			PasswordHash: bcryptPassword,
		}

		userService := controllers.GetUserService()

		if err = userService.Create(&admin); err != nil {
			panic(err)
		}

		bob := models.User{
			Name:         "bob",
			Email:        "bob@example.com",
			PasswordHash: bcryptPassword,
		}

		if err = userService.Create(&bob); err != nil {
			panic(err)
		}

		accountService := controllers.GetAccountService()

		account := models.Account{
			Name:    "Main",
			OwnerId: bob.Id,
		}

		accountService.Create(&account)

		transactionService := controllers.GetTransactionsService()

		transactions := []models.Transaction{
			{UserId: bob.Id, Value: 500},
			{UserId: bob.Id, Value: 600},
			{UserId: bob.Id, Value: 700},
			{UserId: bob.Id, Value: 800},
		}

		for _, transaction := range transactions {
			if err = transactionService.Create(&transaction); err != nil {
				panic(err)
			}
		}

		return nil
	})
}
