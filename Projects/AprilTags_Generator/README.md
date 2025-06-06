# April Tag Generator

## Overview

The April Tag Generator is a simple tool that allows users to generate April tags by selecting a tag family and inputting three different tag IDs. April tags are widely used for robotics and computer vision applications, providing a robust way to encode data visually.

![Program Output](https://github.com/amy-caruana/ac-portfolio/blob/main/Projects/AprilTags_Generator/output.png)

## Features

- Select from various April tag families.
- Input 3 custom tag IDs.
- Quickly generate and save tags for your projects.

## Prerequisites

To use or build upon this project, ensure you have the following installed:

- Python 3.x
- `apriltag` or a similar April Tag generation library (if using an automated generation process)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/amy-caruana/AprilTags_Generator.git
   cd AprilTags_Generator
   ```
2. Install required dependencies:
   ```bash
   pip install -r requirements.txt
   ```

## Usage

1. Run the program:
   ```bash
   python generate_tags.py
   ```
2. Select the desired April tag family from the available options.
3. Enter 3 tag IDs to generate.
4. Generated tags will be displayed and saved in the `output/` directory.

### Supported Tag Families

Examples of supported families include:

- tag16h5
- tag25h9
- tag36h10
- tag36h11
- tagCircle21h7
- tagCircle49h12
- tagCustom48h12
- tagStandard41h12
- tagStandard52h13
  
## File Structure

```
AprilTagGenerator/
|└── 3ID_Generation.py   # Main script for generating April tags
|└── output/            # Directory for generated tags
|└── README.md         # Project documentation
```

## Contributing

Contributions are welcome! If you encounter bugs or have ideas for improvements, please feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. See `LICENSE` for more details.

## Contact

For any questions or inquiries, please contact amycaruana2000\@gmail.com.

