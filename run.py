#!/usr/bin/env python3
"""
Dead Pixel Detective - Build and Run Script

This script provides a convenient way to build and run the Android application.
It wraps common Gradle commands for easier use.

Requirements:
- Python 3.6+
- Android SDK installed
- ANDROID_HOME environment variable set
- Java 17+ installed
"""

import subprocess
import sys
import os
import argparse
from pathlib import Path


def get_project_root():
    """Get the project root directory.

    Returns:
        Path: The absolute path to the project root directory.
    """
    return Path(__file__).parent.absolute()


def run_gradle(command, *args):
    """Run a Gradle command.

    Args:
        command: The Gradle command to execute (e.g., 'assembleDebug').
        *args: Additional arguments to pass to the Gradle command.

    Returns:
        bool: True if the command executed successfully, False otherwise.
    """
    project_root = get_project_root()
    
    # Determine gradlew executable based on OS
    if sys.platform == "win32":
        gradle_exec = project_root / "gradlew.bat"
    else:
        gradle_exec = project_root / "gradlew"
    
    # Build the command
    cmd = [str(gradle_exec), command] + list(args)
    
    print(f"Running: {' '.join(cmd)}")
    print("-" * 50)
    
    try:
        result = subprocess.run(cmd, cwd=project_root)
        return result.returncode == 0
    except FileNotFoundError:
        print(f"Error: Gradle wrapper not found at {gradle_exec}")
        print("Please ensure the Gradle wrapper is set up correctly.")
        return False


def build_debug():
    """Build the debug APK.

    Returns:
        bool: True if the build succeeded, False otherwise.
    """
    print("Building Debug APK...")
    return run_gradle("assembleDebug", "--no-daemon")


def build_release():
    """Build the release APK.

    Returns:
        bool: True if the build succeeded, False otherwise.
    """
    print("Building Release APK...")
    return run_gradle("assembleRelease", "--no-daemon")


def run_tests():
    """Run unit tests.

    Returns:
        bool: True if all tests passed, False otherwise.
    """
    print("Running Unit Tests...")
    return run_gradle("test", "--no-daemon")


def run_lint():
    """Run lint checks.

    Returns:
        bool: True if lint checks passed, False otherwise.
    """
    print("Running Lint...")
    return run_gradle("lint", "--no-daemon")


def install_debug():
    """Install debug APK on connected device.

    Returns:
        bool: True if installation succeeded, False otherwise.
    """
    print("Installing Debug APK...")
    return run_gradle("installDebug", "--no-daemon")


def clean():
    """Clean build outputs.

    Returns:
        bool: True if clean succeeded, False otherwise.
    """
    print("Cleaning build outputs...")
    return run_gradle("clean", "--no-daemon")


def check_environment():
    """Check if the development environment is properly configured.

    Verifies that ANDROID_HOME/ANDROID_SDK_ROOT is set and valid,
    and that Java is installed and accessible.

    Returns:
        bool: True if environment is properly configured, False otherwise.
    """
    issues = []
    
    # Check ANDROID_HOME
    android_home = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    if not android_home:
        issues.append("ANDROID_HOME or ANDROID_SDK_ROOT environment variable not set")
    elif not Path(android_home).exists():
        issues.append(f"Android SDK path does not exist: {android_home}")
    
    # Check Java
    try:
        result = subprocess.run(
            ["java", "-version"],
            capture_output=True,
            text=True
        )
        if result.returncode != 0:
            issues.append("Java not found or not working properly")
    except FileNotFoundError:
        issues.append("Java not found in PATH")
    
    if issues:
        print("Environment Issues Found:")
        for issue in issues:
            print(f"  - {issue}")
        return False
    
    print("Environment check passed!")
    return True


def main():
    """Main entry point for the build and run script.

    Parses command-line arguments and executes the requested build command.
    Supports building, testing, linting, installing, and cleaning operations.
    """
    parser = argparse.ArgumentParser(
        description="Dead Pixel Detective - Build and Run Script",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  python run.py build          Build debug APK
  python run.py test           Run unit tests
  python run.py install        Build and install on device
  python run.py clean          Clean build outputs
  python run.py all            Clean, test, and build
        """
    )
    
    parser.add_argument(
        "command",
        choices=["build", "release", "test", "lint", "install", "clean", "check", "all"],
        help="Command to execute"
    )
    
    args = parser.parse_args()
    
    # Map commands to functions
    commands = {
        "build": build_debug,
        "release": build_release,
        "test": run_tests,
        "lint": run_lint,
        "install": install_debug,
        "clean": clean,
        "check": check_environment,
    }
    
    if args.command == "all":
        # Run full pipeline
        steps = [
            ("Checking environment", check_environment),
            ("Cleaning", clean),
            ("Running tests", run_tests),
            ("Running lint", run_lint),
            ("Building debug APK", build_debug),
        ]
        
        for step_name, step_func in steps:
            print(f"\n{'='*50}")
            print(f"Step: {step_name}")
            print("="*50 + "\n")
            
            if not step_func():
                print(f"\n❌ Failed at: {step_name}")
                sys.exit(1)
        
        print("\n" + "="*50)
        print("✅ All steps completed successfully!")
        print("="*50)
        print("\nDebug APK location: app/build/outputs/apk/debug/app-debug.apk")
    else:
        # Run single command
        success = commands[args.command]()
        sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
