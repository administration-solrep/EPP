import 'package:polymer/builder.dart' as polymer;

main(args) {
  polymer.lint(entryPoints: ['web/index.html', 'test/test.html'], options: polymer.parseOptions(args));
}