import 'package:flutter/material.dart';

class QuickCompareScreen extends StatefulWidget {
  const QuickCompareScreen({super.key});

  @override
  State<QuickCompareScreen> createState() => _QuickCompareScreenState();
}

class _QuickCompareScreenState extends State<QuickCompareScreen> {
  final _formKey = GlobalKey<FormState>();
  double? price;
  double? quantity;
  double? weightPerUnit;
  String unit = 'g';

  final List<String> units = ['g', 'kg', 'mL', 'L'];

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
          '⚡ Quick Compare',
          style: TextStyle(
            color: Colors.black87,
            fontWeight: FontWeight.bold,
            fontSize: 24,
            letterSpacing: 1.0,
          ),
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Form(
          key: _formKey,
          child: Column(
            children: [
              buildTextField(
                label: 'Price (€)',
                keyboardType: TextInputType.number,
                validator: (value) => value == null || value.isEmpty ? 'Enter price' : null,
                onSaved: (value) => price = double.tryParse(value ?? '0'),
              ),
              const SizedBox(height: 16),
              buildTextField(
                label: 'Quantity',
                keyboardType: TextInputType.number,
                validator: (value) => value == null || value.isEmpty ? 'Enter quantity' : null,
                onSaved: (value) => quantity = double.tryParse(value ?? '0'),
              ),
              const SizedBox(height: 16),
              buildTextField(
                label: 'Weight or Volume',
                keyboardType: TextInputType.number,
                validator: (value) => value == null || value.isEmpty ? 'Enter weight/volume' : null,
                onSaved: (value) => weightPerUnit = double.tryParse(value ?? '0'),
              ),
              const SizedBox(height: 24),
              DropdownButtonFormField<String>(
                value: unit,
                decoration: InputDecoration(
                  labelText: 'Unit',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(12),
                  ),
                  filled: true,
                  fillColor: Colors.white,
                ),
                dropdownColor: Colors.white,
                style: const TextStyle(
                  color: Colors.black87,
                  fontSize: 16,
                ),
                iconEnabledColor: Colors.black87,
                borderRadius: BorderRadius.circular(12),
                items: units.map((u) {
                  return DropdownMenuItem(
                    value: u,
                    child: Text(u),
                  );
                }).toList(),
                onChanged: (value) {
                  setState(() {
                    unit = value!;
                  });
                },
              ),
              const SizedBox(height: 32),
              SizedBox(
                width: double.infinity,
                child: Material(
                  color: const Color(0xFF268E77),
                  borderRadius: BorderRadius.circular(12),
                  child: InkWell(
                    borderRadius: BorderRadius.circular(12),
                    onTap: () {
                      if (_formKey.currentState!.validate()) {
                        _formKey.currentState!.save();
                        Navigator.pop(context, {
                          'name': 'Unnamed Product', // Quick Compare skips name
                          'price': (price ?? 0).toDouble(),
                          'quantity': (quantity ?? 1).toDouble(), // Quick Compare assumes 1 unit if not provided
                          'weightPerUnit': (weightPerUnit ?? 1).toDouble(),
                          'unit': unit,
                          'discount': 0, // Quick Compare assumes no discount
                        });
                      }
                    },
                    child: Container(
                      padding: const EdgeInsets.symmetric(vertical: 16),
                      alignment: Alignment.center,
                      child: const Text(
                        'Save',
                        style: TextStyle(
                          fontSize: 18,
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget buildTextField({
    required String label,
    TextInputType keyboardType = TextInputType.text,
    String? Function(String?)? validator,
    required void Function(String?) onSaved,
  }) {
    return TextFormField(
      decoration: InputDecoration(
        labelText: label,
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
        filled: true,
        fillColor: Colors.white,
      ),
      keyboardType: keyboardType,
      validator: validator,
      onSaved: onSaved,
    );
  }
}
