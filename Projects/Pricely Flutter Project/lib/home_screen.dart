import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'history_screen.dart';
import 'favorites_screen.dart';


class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final GlobalKey<AnimatedListState> _listKey = GlobalKey<AnimatedListState>();
  final ScrollController _scrollController = ScrollController();
  List<Map<String, dynamic>> products = [];
  List<Map<String, dynamic>> historyProducts = [];
  List<Map<String, dynamic>> favorites = [];

  bool isLoading = true;

  Map<String, dynamic>? lastAddedProduct;

  @override
  void initState() {
    super.initState();
    loadAllData();
    loadProducts();
    loadHistory();
    loadFavorites();
  }

    Future<void> loadAllData() async {
      await loadProducts();
      await loadHistory();
      await loadFavorites();
      
      setState(() {
        isLoading = false;
      });
    }

  Future<void> loadHistory() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? data = prefs.getString('history');
    if (data != null) {
      setState(() {
        historyProducts = List<Map<String, dynamic>>.from(jsonDecode(data));
      });
    }
  }

  Future<void> loadProducts() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? data = prefs.getString('products');
    if (data != null) {
      setState(() {
        products = List<Map<String, dynamic>>.from(jsonDecode(data));
      });
    }
  }

  Future<void> saveProducts() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('products', jsonEncode(products));
  }

  Future<void> saveHistory() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('history', jsonEncode(historyProducts));
  }

    Future<void> saveFavorites() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('favorites', jsonEncode(favorites));
  }

  Future<void> loadFavorites() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    String? data = prefs.getString('favorites');
    if (data != null) {
      setState(() {
        favorites = List<Map<String, dynamic>>.from(jsonDecode(data));
      });
    }
  }

  double normalizeWeight(double weight, String unit) {
    if (unit == 'kg' || unit == 'L') return weight * 1000;
    return weight;
  }

double calculatePricePerUnit(Map<String, dynamic> product) {
    double originalPrice = (product['price'] as num).toDouble();
    double discount = (product['discount'] ?? 0 as num).toDouble();
    double quantity = (product['quantity'] ?? 1 as num).toDouble();
    double rawWeight = (product['weightPerUnit'] ?? 1 as num).toDouble();
    String unit = product['unit'] ?? 'g';
    double normalized = normalizeWeight(rawWeight, unit);
    double effectiveWeight = quantity * normalized;
    return (originalPrice - (originalPrice * discount / 100)) / effectiveWeight;
}


  String formatPricePerUnit(double value, String unit) {
    if (unit == 'g' || unit == 'kg') {
      return '€${(value * 1000).toStringAsFixed(2)} per kg';
    } else if (unit == 'mL' || unit == 'L') {
      return '€${(value * 1000).toStringAsFixed(2)} per L';
    } else {
      return '€${value.toStringAsFixed(2)} per unit';
    }
  }


  @override
  Widget build(BuildContext context) {
    double? cheapestPrice;
    if (products.isNotEmpty) {
      cheapestPrice = products
          .map((p) => calculatePricePerUnit(p))
          .reduce((a, b) => a < b ? a : b);
    }

     if (isLoading) {
        return const Scaffold(
          backgroundColor: Color(0xFFFAFAFA),
          body: Center(
            child: CircularProgressIndicator(),
          ),
        );
      }

    return Scaffold(
      backgroundColor: const Color(0xFFFAFAFA),
      appBar: AppBar(
      backgroundColor: Colors.white,
      centerTitle: true,
      elevation: 2,
      flexibleSpace: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFFE3F2FD), Color(0xFFFFFFFF)], // Light blue to white
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
      ),
      title: const Text(
        '🛒 Compare Prices',
        style: TextStyle(
          color: Colors.black87,
          fontWeight: FontWeight.bold,
          fontSize: 24,
          letterSpacing: 1.0,
        ),
      ),
      actions: [
        IconButton(
          icon: const Icon(Icons.delete_outline, color: Colors.black87),
          onPressed: () {
            if (products.isNotEmpty) {
              showDialog(
                context: context,
                builder: (context) => AlertDialog(
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                  title: Row(
                    children: const [
                      Icon(Icons.warning_amber_rounded, color: Colors.amber, size: 30),
                      SizedBox(width: 8),
                      Text('Clear All?', style: TextStyle(fontWeight: FontWeight.bold)),
                    ],
                  ),
                  content: const Text(
                    'Are you sure you want to delete all products?\nThis cannot be undone.',
                    style: TextStyle(fontSize: 16),
                  ),
                  actions: [
                    TextButton(
                      onPressed: () => Navigator.pop(context),
                      child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
                    ),
                    ElevatedButton(
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.black,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                      ),
                      onPressed: () {
                        setState(() {
                          products.clear();
                        });
                        saveProducts();
                        Navigator.pop(context);
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('All products cleared')),
                        );
                      },
                      child: const Text('Confirm', style: TextStyle(color: Colors.white)),
                    ),
                  ],
                ),
              );
            }
          },
        ),
        IconButton(
          icon: const Icon(Icons.history),
          onPressed: () async{
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => HistoryScreen(historyProducts: historyProducts),
              ),
            );
            await loadHistory(); // reload after returning
            setState(() {});
          },
        ),
        IconButton(
          icon: const Icon(Icons.star, color: Colors.amber),
          onPressed: () async {
            await Navigator.push(
              context,
              MaterialPageRoute(
                builder: (context) => FavoritesScreen(
                  favorites: favorites,
                  onFavoritesUpdated: () async {
                    await loadFavorites();
                    setState(() {});
                  },
                ),
              ),
            );
            await loadFavorites();
            setState(() {});
          },
        )
      ],
    ),

      body: products.isEmpty
          ? Center(
              child: Card(
                margin: const EdgeInsets.symmetric(horizontal: 30),
                color: Colors.blue.shade50,
                elevation: 4,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(Icons.local_offer_outlined, size: 48, color: Colors.blueAccent),
                      const SizedBox(height: 16),
                      const Text('No products yet!',
                          style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                      const SizedBox(height: 10),
                      const Text('Let\'s start comparing and saving money 💸',
                          textAlign: TextAlign.center,
                          style: TextStyle(fontSize: 16, color: Colors.black54)),
                      const SizedBox(height: 20),
                      ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(0xFF33596A),
                          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                        ),
                        onPressed: () async {
                          var result = await Navigator.pushNamed(context, '/add');
                          if (result != null) {
                            var newProduct = result as Map<String, dynamic>;
                            setState(() {
                              newProduct['timestamp'] = DateTime.now().toIso8601String();

                              products.add(newProduct);
                              historyProducts.add(newProduct);
                              products.sort((a, b) => calculatePricePerUnit(a).compareTo(calculatePricePerUnit(b)));
                              lastAddedProduct = newProduct;
                            });
                            saveProducts();
                            saveHistory();

                            WidgetsBinding.instance.addPostFrameCallback((_) {
                              _listKey.currentState?.insertItem(products.indexOf(newProduct) + 1);

                              _scrollController.animateTo(
                                _scrollController.position.maxScrollExtent,
                                duration: const Duration(milliseconds: 500),
                                curve: Curves.easeOut,
                              );

                              Future.delayed(const Duration(seconds: 1), () {
                                setState(() {
                                  lastAddedProduct = null;
                                });
                              });
                            });
                          }
                        },
                        icon: const Icon(Icons.add, color: Colors.white),
                        label: const Text('Add Product', style: TextStyle(color: Colors.white)),
                      ),
                    ],
                  ),
                ),
              ),
            )
          : AnimatedList(
              key: _listKey,
              controller: _scrollController,
              initialItemCount: products.length + 1,
              itemBuilder: (context, index, animation) {
                if (index == 0) {
                  return FadeTransition(
                    opacity: animation,
                    child: SizeTransition(sizeFactor: animation, child: buildSummaryCard()),
                  );
                }

                if (index - 1 >= products.length) return const SizedBox.shrink();

                var product = products[index - 1];
                bool isNew = (product == lastAddedProduct);
                double pricePerUnit = calculatePricePerUnit(product);
                bool isCheapest = pricePerUnit == cheapestPrice;

                return Dismissible(
                  key: Key(product.toString()),
                  direction: DismissDirection.endToStart,
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    color: Colors.red,
                    child: const Icon(Icons.delete, color: Colors.white),
                  ),
                  onDismissed: (direction) {
                    setState(() {
                      products.removeAt(index - 1);
                    });
                    _listKey.currentState!.removeItem(
                      index,
                      (context, animation) => const SizedBox.shrink(),
                    );
                    saveProducts();
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Product deleted')),
                    );
                  },
                  child: buildProductTile(product, pricePerUnit, isCheapest, isNew: isNew),
                );
              },
            ),
      floatingActionButton: Column(
        mainAxisAlignment: MainAxisAlignment.end,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          FloatingActionButton.extended(
            heroTag: 'quickCompareButton',
            backgroundColor: Color(0xFF33596A),
            onPressed: () async {
              var result = await Navigator.pushNamed(context, '/quick');
              if (result != null) {
                var newProduct = result as Map<String, dynamic>;
                setState(() {
                  newProduct['timestamp'] = DateTime.now().toIso8601String();

                  products.add(newProduct);
                  historyProducts.add(newProduct);
                  products.sort((a, b) =>
                      calculatePricePerUnit(a).compareTo(calculatePricePerUnit(b)));
                  lastAddedProduct = newProduct;
                });
                WidgetsBinding.instance.addPostFrameCallback((_) {
                  int newIndex = products.indexOf(newProduct) + 1;
                  _listKey.currentState?.insertItem(newIndex);
                  _scrollController.animateTo(
                    _scrollController.position.maxScrollExtent,
                    duration: const Duration(milliseconds: 500),
                    curve: Curves.easeOut,
                  );
                  Future.delayed(const Duration(seconds: 1), () {
                    setState(() {
                      lastAddedProduct = null;
                    });
                  });
                });
                saveProducts();
                saveHistory();
              }
            },
            icon: const Icon(Icons.flash_on, color: Colors.white),
            label: const Text('Quick Compare', style: TextStyle(color: Colors.white)),
          ),
          const SizedBox(height: 12),
          FloatingActionButton(
            heroTag: 'addProductButton',
            backgroundColor: Color(0xFF33596A),
            onPressed: () async {
              var result = await Navigator.pushNamed(context, '/add');
              if (result != null) {
                var newProduct = result as Map<String, dynamic>;
                setState(() {
                  newProduct['timestamp'] = DateTime.now().toIso8601String();

                  products.add(newProduct);
                  historyProducts.add(newProduct);
                  products.sort((a, b) =>
                      calculatePricePerUnit(a).compareTo(calculatePricePerUnit(b)));
                  lastAddedProduct = newProduct;
                });
                WidgetsBinding.instance.addPostFrameCallback((_) {
                  int newIndex = products.indexOf(newProduct) + 1;
                  _listKey.currentState?.insertItem(newIndex);
                  _scrollController.animateTo(
                    _scrollController.position.maxScrollExtent,
                    duration: const Duration(milliseconds: 500),
                    curve: Curves.easeOut,
                  );
                  Future.delayed(const Duration(seconds: 1), () {
                    setState(() {
                      lastAddedProduct = null;
                    });
                  });
                });
                saveProducts();
                saveHistory();
              }
            },
            child: const Icon(Icons.add, color: Colors.white),
          ),
        ],
      ),
    );
  }

    Widget buildSummaryCard() {
    if (products.isEmpty) return const SizedBox.shrink();

    double bestPrice = double.infinity;
    String? bestProduct;
    String? bestProductUnit;

    double totalPricePerUnit = 0;

    for (var product in products) {
      double pricePerUnit = calculatePricePerUnit(product);
      totalPricePerUnit += pricePerUnit;
    if (pricePerUnit < bestPrice) {
      bestPrice = pricePerUnit;
      bestProduct = product['name'] ?? 'Unnamed Product';
      bestProductUnit = product['unit'] ?? 'g';
      }
    }

    double average = totalPricePerUnit / products.length;

    return Card(
    color: Colors.blue.shade50,
    margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
    elevation: 6,
    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
    child: Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text(
            '📋 Summary',
            style: TextStyle(
              fontSize: 22,
              fontWeight: FontWeight.bold,
              letterSpacing: 1.0,
            ),
          ),
          const SizedBox(height: 20),
          Row(
            children: [
              const Text(
                '🏆 Best Deal:',
                style: TextStyle(fontSize: 17, fontWeight: FontWeight.bold),
              ),
              const SizedBox(width: 8),
              Expanded(
                child: Text(
                  bestProduct ?? '',
                  style: const TextStyle(
                    fontSize: 17,
                    fontWeight: FontWeight.w500,
                    color: Colors.green,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ],
          ),
          const SizedBox(height: 10),
          Row(
            children: [
              const Text(
                '💶 Price:',
                style: TextStyle(fontSize: 16),
              ),
              const SizedBox(width: 8),
              Text(
                //'€${(bestPrice * 1000).toStringAsFixed(2)} per kg/L',
                formatPricePerUnit(bestPrice, bestProductUnit ?? 'g'),
                style: const TextStyle(fontSize: 16, color: Colors.black87),
              ),
            ],
          ),
          const SizedBox(height: 10),
          Row(
            children: [
              const Text(
                '📊 Average:',
                style: TextStyle(fontSize: 16),
              ),
              const SizedBox(width: 8),
              Text(
                //'€${(average * 1000).toStringAsFixed(2)} per kg/L',
                formatPricePerUnit(average, bestProductUnit ?? 'g'),
                style: const TextStyle(fontSize: 16, color: Colors.black54),
              ),
            ],
          ),
        ],
      ),
    ),
    );
  }

  Widget buildProductTile(Map<String, dynamic> product, double pricePerUnit, bool isCheapest, {bool isNew = false}) {
    String unit = product['unit'] ?? 'g';
    double originalPrice = product['price'];

    return AnimatedContainer(
      duration: const Duration(milliseconds: 600),
      curve: Curves.easeOut,
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: isNew ? Colors.yellow.shade100 : Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: const [
          BoxShadow(color: Colors.black12, blurRadius: 6, offset: Offset(0, 2)),
        ],
      ),
      child: ListTile(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        title: Row(
          children: [
            Expanded(
              child: Text(
                product['name'] ?? 'Unnamed Product',
                style: TextStyle(
                  fontSize: 16,
                  color: isCheapest ? Colors.green.shade700 : Colors.black,
                  fontWeight: isCheapest ? FontWeight.bold : FontWeight.normal,
                ),
              ),
            ),
            if ((product['discount'] ?? 0) > 0)
              Chip(
                label: const Text('Discount', style: TextStyle(fontSize: 10, color: Colors.white)),
                backgroundColor: Colors.orange.shade400,
              ),
          ],
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              formatPricePerUnit(pricePerUnit, unit),
              style: TextStyle(
                fontWeight: FontWeight.w500,
                fontSize: 15,
                color: isCheapest ? Colors.green.shade700 : Colors.black,
              ),
            ),
            const SizedBox(height: 4),
            Text('Original: €${originalPrice.toStringAsFixed(2)}',
                style: const TextStyle(fontSize: 13, color: Colors.black54)),
          ],
        ),
        trailing: IconButton(
          icon: Icon(
            favorites.contains(product) ? Icons.star : Icons.star_border,
            color: favorites.contains(product) ? Colors.amber : Colors.grey,
          ),
          onPressed: () {
            setState(() {
              if (favorites.contains(product)) {
                favorites.remove(product);
              } else {
                favorites.add(product);
              }
              saveFavorites();
            });
          },
        )
      ),
    );
  }
}
