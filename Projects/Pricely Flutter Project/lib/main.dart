import 'package:flutter/material.dart';
import 'home_screen.dart';
import 'add_product_screen.dart';
import 'quick_compare_screen.dart';

void main() {
  runApp(const PriceComparatorApp());
}

class PriceComparatorApp extends StatelessWidget {
  const PriceComparatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Price Comparator',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const HomeScreen(),
      routes: {
        '/add': (context) => const AddProductScreen(),
        '/quick': (context) => const QuickCompareScreen(),
      },
    );
  }
}
