import numpy as np
from matplotlib import pyplot as plt
from moms_apriltag import TagGenerator3
from opencv_camera import mosaic
import cv2
import os

# Create an output directory for the tags
output_folder = "apriltags_generated"
os.makedirs(output_folder, exist_ok=True)

# Initialize the TagGenerator3 with a specific tag family
tag_family = "tagStandard41h12"  # You can change this to other families like "tagCircle49h12"
tg = TagGenerator3(tag_family)
# Generate 3 tags with specific IDs
tag_ids = [231, 232, 233]  # You can change these to other IDs
imgs = []
for tag_id in tag_ids:
    im = tg.generate(tag_id)  # Generate the AprilTag for the given ID
    imgs.append(im)

    # Convert the tag to RGBA format for saving
    png = tg.toRGBA(im)

    # Save the tag as a PNG file
    tag_filename = os.path.join(output_folder, f"apriltag_{tag_family}_{tag_id}.png")
    cv2.imwrite(tag_filename, png)
    print(f"Generated and saved AprilTag ID {tag_id} as {tag_filename}")

    # Display the tag using matplotlib
    plt.imshow(im, cmap="gray")
    plt.title(f"AprilTag ID {tag_id}")
    plt.axis("off")
    plt.show()
