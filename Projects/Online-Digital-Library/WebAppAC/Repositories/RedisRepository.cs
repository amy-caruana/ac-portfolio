using Newtonsoft.Json;
using StackExchange.Redis;
using WebAppAC.Models;

namespace WebAppAC.Repositories
{
    public class RedisRepository
    {

        private IDatabase db;

        public RedisRepository()
        {
            //creating the connection string
            var options = "redis-10362.c327.europe-west1-2.gce.redns.redis-cloud.com:10362,password=2rRPRyLBRDlJxgSfEWOquSRCnP069xJN,abortConnect=false";
            var connection = ConnectionMultiplexer.Connect(options);

            db = connection.GetDatabase();
        }

        public async Task SetMenus(List<Menu> menus)
        {
            // Serialize the menu list to JSON
            string menusJson = JsonConvert.SerializeObject(menus);

            // Set the JSON string in Redis with a specific key
            await db.StringSetAsync("menus", menusJson);
        }


        public async Task<List<Menu>> GetMenus()
        {
            try
            {
                string jsonMenus = await db.StringGetAsync("menus");
                if (!string.IsNullOrEmpty(jsonMenus))
                {
                    //deserializing the JSON string to list of Menu objects
                    List<Menu> menus = JsonConvert.DeserializeObject<List<Menu>>(jsonMenus);
                    return menus;
                }
                return new List<Menu>();
            }
            catch (RedisConnectionException ex)
            {
                Console.WriteLine("Redis connection error: " + ex.Message);
                return new List<Menu>();
            }
            catch (Exception ex)
            {
                //generic exception handling
                Console.WriteLine("Error fetching menus: " + ex.Message);
                return new List<Menu>();
            }
        }
    }
}
