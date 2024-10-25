using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.Google;
using StackExchange.Redis;
using WebAppAC.Repositories;

namespace WebAppAC
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            string pathToKeyFile = builder.Environment.ContentRootPath + "";
            System.Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", pathToKeyFile);

            builder.Services
                .AddAuthentication(options =>
                {
                    options.DefaultScheme = CookieAuthenticationDefaults.AuthenticationScheme;
                    options.DefaultChallengeScheme = GoogleDefaults.AuthenticationScheme;
                })
                .AddCookie()
                .AddGoogle(options =>
                {
                    options.ClientId = "";//builder.Configuration["Authentication:Google:ClientId"];
                    options.ClientSecret = "";//builder.Configuration["Authentication:Google:ClientSecret"];
                });

            builder.Services.Configure<CookiePolicyOptions>(options =>
            {
                options.MinimumSameSitePolicy = SameSiteMode.Unspecified;
                options.OnAppendCookie = cookieContext =>
                    CheckSameSite(cookieContext.Context, cookieContext.CookieOptions);
                options.OnDeleteCookie = cookieContext =>
                    CheckSameSite(cookieContext.Context, cookieContext.CookieOptions);
            });

            string project = builder.Configuration["project"].ToString();
            string bucket = builder.Configuration["bucket"].ToString();
            string pubsub = builder.Configuration["pubsub"].ToString();

            builder.Services.AddRazorPages();
            builder.Services.AddScoped<DocumentsRepository>(x=>new DocumentsRepository(project));
            builder.Services.AddScoped<UsersRepository>(x => new UsersRepository(project));
            builder.Services.AddScoped<BucketsRepository>(x => new BucketsRepository(project, bucket));
            builder.Services.AddScoped<PubSubRepository>(x => new PubSubRepository(project, pubsub));
            builder.Services.AddScoped<RedisRepository>();

            //builder.Services.AddScoped<IFontResolver, FileFontResolver>();

            // Add services to the container.
            builder.Services.AddControllersWithViews();

            var app = builder.Build();

            // Configure the HTTP request pipeline.
            if (!app.Environment.IsDevelopment())
            {
                app.UseExceptionHandler("/Home/Error");
                // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
                app.UseHsts();
            }

            app.UseHttpsRedirection();
            app.UseStaticFiles();

            app.UseRouting();

            app.UseCookiePolicy();
            app.UseAuthentication();
            app.UseAuthorization();

            // Middleware in order to add menus to ViewData
            app.Use(async (context, next) =>
            {
                var redisRepository = context.RequestServices.GetService<RedisRepository>();
                try
                {
                    var menus = await redisRepository.GetMenus();
                    context.Items["Menus"] = menus;
                }
                catch (RedisConnectionException ex)
                {
                    Console.WriteLine("Error connecting to Redis: " + ex.Message);
                }
                catch (Exception ex)
                {
                    Console.WriteLine("General error: " + ex.Message);
                }

                await next();
            });

            app.MapControllerRoute(
                name: "default",
                pattern: "{controller=Home}/{action=Index}/{id?}");

            app.Run();
        }

        private static void CheckSameSite(HttpContext httpContext, CookieOptions options)
        {
            if (options.SameSite == SameSiteMode.None)
            {
                var userAgent = httpContext.Request.Headers["User-Agent"].ToString();

                if (true)
                {
                    // For .NET Core < 3.1 set SameSite = (SameSiteMode)(-1)
                    options.SameSite = SameSiteMode.Unspecified;
                }
            }
        }
    }
}