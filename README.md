# csv to markdown convertor

A simple csv to markdown convertor for easier viewing of a csv.

## Usage

Requires `java` to be installed.

Download latest artefact and set executable. Running from the commandline will display usage information.

- Prints usage:
    - `md-to-csv -h`
- Convert single file:
    - `md-to-csv -f test-data/csv-1.csv`
- Convert all csv files in a directory (`~/Downloads`):
    - `md-to-csv -d ~/Downloads`    

### Development

Tool: `scala-cli`

Commands:

- Prints usage:
    - `scala-cli run main.scala csv-to-md.scala -- -h`
- Convert single file:
    - `scala-cli run main.scala csv-to-md.scala -- -f test-data/csv-1.csv`
- Convert all csv files in a directory (`~/Downloads`):
    - `scala-cli run main.scala csv-to-md.scala -- -d ~/Downloads`    
- Run tests:
    - `scala-cli test . --watch`    
