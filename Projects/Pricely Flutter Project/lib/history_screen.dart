import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import 'package:intl/intl.dart'; // for timestamp formatting

class HistoryScreen extends StatefulWidget {
  final List<Map<String, dynamic>> historyProducts;

  const HistoryScreen({super.key, required this.historyProducts});

  @override
  State<HistoryScreen> createState() => _HistoryScreenState();
}

class _HistoryScreenState extends State<HistoryScreen> {
  late List<Map<String, dynamic>> _localHistory; // Local copy

  @override
  void initState() {
    super.initState();
    _localHistory = List<Map<String, dynamic>>.from(widget.historyProducts);
  }

  Future<void> saveHistory() async {
    SharedPreferences prefs = await SharedPreferences.getInstance();
    await prefs.setString('history', jsonEncode(_localHistory));
  }

  String formatTimestamp(String timestamp) {
    try {
      DateTime dateTime = DateTime.parse(timestamp);
      return DateFormat('dd MMM yyyy, HH:mm').format(dateTime);
    } catch (e) {
      return '';
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
          '🕘 History',
          style: TextStyle(
            color: Colors.black87,
            fontWeight: FontWeight.bold,
            fontSize: 24,
            letterSpacing: 1.0,
          ),
        ),
        actions: [
          if (_localHistory.isNotEmpty)
            IconButton(
              icon: const Icon(Icons.delete_forever, color: Colors.redAccent),
              onPressed: () async {
                bool? confirm = await showDialog(
                  context: context,
                  builder: (context) => AlertDialog(
                    title: const Text('Clear all history?'),
                    content: const Text('This cannot be undone.'),
                    actions: [
                      TextButton(
                        child: const Text('Cancel'),
                        onPressed: () => Navigator.pop(context, false),
                      ),
                      TextButton(
                        child: const Text('Confirm', style: TextStyle(color: Colors.red)),
                        onPressed: () => Navigator.pop(context, true),
                      ),
                    ],
                  ),
                );

                if (confirm == true) {
                  setState(() {
                    _localHistory.clear();
                  });
                  await saveHistory();
                  Navigator.pop(context); // go back to HomeScreen
                }
              },
            )
        ],
      ),
      body: _localHistory.isEmpty
          ? Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: const [
                  Icon(Icons.history, size: 80, color: Colors.grey),
                  SizedBox(height: 20),
                  Text('No history yet!', style: TextStyle(fontSize: 20, fontWeight: FontWeight.w600)),
                  SizedBox(height: 10),
                  Text('Start adding some products 🛒', style: TextStyle(fontSize: 16, color: Colors.black54)),
                ],
              ),
            )
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: _localHistory.length,
              itemBuilder: (context, index) {
                var product = _localHistory.reversed.toList()[index];
                return Dismissible(
                  key: Key(product.toString()),
                  direction: DismissDirection.endToStart,
                  background: Container(
                    alignment: Alignment.centerRight,
                    padding: const EdgeInsets.symmetric(horizontal: 20),
                    color: Colors.redAccent,
                    child: const Icon(Icons.delete, color: Colors.white),
                  ),
                  onDismissed: (direction) async {
                    setState(() {
                      _localHistory.remove(product);
                    });
                    await saveHistory(); // 👈 save immediately!
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
                              '🗓 Added: ${formatTimestamp(product['timestamp'])}',
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
