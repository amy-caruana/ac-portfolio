import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';

class FavoritesScreen extends StatefulWidget {
  final List<Map<String, dynamic>> favorites;
  final VoidCallback onFavoritesUpdated;

  const FavoritesScreen({super.key, required this.favorites, required this.onFavoritesUpdated});

  @override
  State<FavoritesScreen> createState() => _FavoritesScreenState();
}

class _FavoritesScreenState extends State<FavoritesScreen> {
  late List<Map<String, dynamic>> favoritesList;

  @override
  void initState() {
    super.initState();
    favoritesList = List<Map<String, dynamic>>.from(widget.favorites);
  }

  Future<void> saveFavorites() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('favorites', jsonEncode(favoritesList));
  }

  void clearFavorites() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.remove('favorites');
    setState(() {
      favoritesList.clear();
    });
    widget.onFavoritesUpdated();
    Navigator.pop(context); // Close screen if empty
  }

  void removeFavorite(int index) async {
    setState(() {
      favoritesList.removeAt(index);
    });
    await saveFavorites();
    widget.onFavoritesUpdated();

    if (mounted && favoritesList.isEmpty) {
      Navigator.pop(context);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFFAFAFA),
      appBar: AppBar(
        centerTitle: true,
        elevation: 2,
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [Color(0xFFE3F2FD), Color(0xFFFFFFFF)],
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
            ),
          ),
        ),
        title: const Text(
          '⭐ Favorites',
          style: TextStyle(
            color: Colors.black87,
            fontWeight: FontWeight.bold,
            fontSize: 24,
            letterSpacing: 1.0,
          ),
        ),
        actions: [
          if (favoritesList.isNotEmpty)
            IconButton(
              icon: const Icon(Icons.delete_forever, color: Colors.black87),
              onPressed: () {
                showDialog(
                  context: context,
                  builder: (context) => AlertDialog(
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                    title: const Text('Clear All Favorites?'),
                    content: const Text('This will remove all favorites.'),
                    actions: [
                      TextButton(
                        child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
                        onPressed: () => Navigator.pop(context),
                      ),
                      ElevatedButton(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: Colors.black,
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                        ),
                        onPressed: () {
                          Navigator.pop(context);
                          clearFavorites();
                        },
                        child: const Text('Confirm', style: TextStyle(color: Colors.white)),
                      ),
                    ],
                  ),
                );
              },
            )
        ],
      ),
      body: favoritesList.isEmpty
          ? const Center(
              child: Text(
                'No favorites yet!\n⭐ Start favoriting products!',
                textAlign: TextAlign.center,
                style: TextStyle(fontSize: 18, color: Colors.black54),
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: favoritesList.length,
              itemBuilder: (context, index) {
                var product = favoritesList[index];
                return Dismissible(
                  key: ValueKey(product.toString()),
                  direction: DismissDirection.endToStart,
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    color: Colors.orangeAccent,
                    child: const Icon(Icons.star_border, color: Colors.white),
                  ),
                  onDismissed: (direction) {
                    removeFavorite(index);
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Removed from favorites ⭐')),
                    );
                  },
                  child: Card(
                    margin: const EdgeInsets.symmetric(vertical: 8),
                    elevation: 5,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(16),
                    ),
                    child: ListTile(
                      contentPadding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                      title: Text(
                        product['name'] ?? 'Unnamed Product',
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: Colors.black87,
                        ),
                      ),
                      subtitle: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const SizedBox(height: 8),
                          Text(
                            '💶 Price: €${(product['price'] as num).toStringAsFixed(2)}',
                            style: const TextStyle(fontSize: 16, color: Colors.black87),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            '📦 Quantity: ${(product['quantity'] as num).toStringAsFixed(0)} unit(s)',
                            style: const TextStyle(fontSize: 15, color: Colors.black54),
                          ),
                          const SizedBox(height: 6),
                          if (product.containsKey('timestamp'))
                            Text(
                              '🗓 Saved: ${DateTime.parse(product['timestamp']).toLocal().toString().substring(0, 16)}',
                              style: const TextStyle(fontSize: 14, color: Colors.grey),
                            ),
                        ],
                      ),
                    ),
                  ),
                );
              },
            ),
    );
  }
}
