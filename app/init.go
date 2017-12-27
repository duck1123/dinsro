package app

import (
	"github.com/duck1123/dinsro/app/controllers"
	"github.com/duck1123/dinsro/app/models"
	"github.com/revel/modules/orm/gorp/app"
	"github.com/revel/revel"
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

		dbmap.AddTableWithName(models.Transaction{}, "transactions")
		dbmap.AddTableWithName(models.User{}, "users")
		dbmap.CreateTables()

		var err error

		admin := models.User{Name: "admin", Email: "admin@example.com"}
		if err = dbmap.Insert(&admin); err != nil {
			panic(err)
		}

		bob := models.User{Name: "bob", Email: "bob@example.com"}
		if err = dbmap.Insert(&bob); err != nil {
			panic(err)
		}

		transactionService := controllers.GetTransactionsService()

		transaction := models.Transaction{UserId: bob.Id}

		if err = transactionService.Create(&transaction); err != nil {
			panic(err)
		}

		return nil
	})
}
