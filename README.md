<div align="center">
  <p>
    <h1>
      <a href="https://github.com/6eero/NewPass">
        <img src="https://github.com/6eero/NewPass/assets/114809573/77aeeea8-5440-433b-8621-2a5b54173896" width="50" title="NewPass" />
      </a>
      <br />
      NewPass
    </h1>
    <h4>Create a strong password for your digital realm.</h4>
    <h4></h4>
  </p>
  <p>
  </p>
</div>

<p align="right">
   <img src="https://github.com/6eero/NewPass/assets/114809573/da89a98d-585b-443f-a2ee-6fbb592fbad5" title="UI">
</p>


# 📍Intro
NewPass is a secure password management application designed to generate and store strong passwords locally on your device. With NewPass, you can create highly secure passwords for your accounts and services without the need to remember them.


## 🗝️ Key Features:
- **Password Generation**: NewPass provides a robust password generator that allows you to create complex and secure passwords tailored to your specific requirements. You can customize the length and the character set (Uppercase, Numbers and Special).

- **Local Storage**: Your passwords are stored locally on your device, ensuring complete privacy and control over your data. NewPass does not store any passwords on external servers, minimizing the risk of unauthorized access (If you uninstall the app, your password are lost!).

- **AES Encryption**: NewPass encrypts all stored passwords using Advanced Encryption Standard (AES) with Cipher Block Chaining (CBC) mode before saving them in the local database.

- **User-Friendly Interface**: NewPass features an intuitive and user-friendly interface, making it easy to generate, view, and manage your passwords. The app offers convenient options for copying passwords to the clipboard and securely sharing them with other applications.


## ⬇️ Installation
To use NewPass, install the apk from [release](https://github.com/6eero/NewPass/releases)

...or follow these steps:
1. Clone this repository.
```
git clone https://github.com/6eero/NewPass.git
```
2. Open the project in Android Studio.
3. Build and run the app on your Android device or emulator.
4. Launch NewPass.
  

## ⚒️ Todo
- [x] Fix random crashes with multiple users
- [x] Improve the login GUI.
- [x] Add a login menu to unlock the application.
- [x] Currently the encryption key and IV vector are hardcoded into the source code. I have to find a way to mask them.
- [x] Add encryption: The app does not currently provide secrecy restrictions on entered passwords.
