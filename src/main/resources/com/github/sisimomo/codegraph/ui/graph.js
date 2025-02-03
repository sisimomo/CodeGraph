// ✅ Ensure dagre is registered
cytoscape.use(cytoscapeDagre);

class GraphManager {
    constructor(containerId) {
        this.cy = null;
        this.graphData = {};
        this.disabledNodes = new Set();
        this.containerId = containerId;
        this.isReady = false;

        window.onload = () => {
            this.isReady = true;
        };
    }

    initializeGraph(data) {
        this.graphData = data;

        this.cy = cytoscape({
            container: document.getElementById(this.containerId),
            elements: [...data.nodes, ...data.edges],
            style: this.getStyles(),
            layout: this.getLayoutConfig()
        });

        this.attachEventListeners();
    }

    getStyles() {
        return [
            {
                selector: 'node',
                style: {
                    'shape': 'roundrectangle',
                    'background-color': '#0074D9',
                    'color': '#FFFFFF',
                    'font-size': '16px',
                    'text-valign': 'center',
                    'text-halign': 'center',
                    'text-wrap': 'wrap',
                    'text-max-width': '120px',
                    'label': 'data(label)',
                    'text-opacity': 1,
                    'padding': '8px',
                    'width': (ele) => Math.max(120, ele.data('label').length * 9),
                    'height': (ele) => Math.max(50, 25 + (ele.data('label').length * 1.2))
                }
            },
            {
                selector: 'edge',
                style: {
                    'width': 3,
                    'arrow-scale': 2,
                    'line-color': '#FFFFFF',
                    'target-arrow-color': '#FFFFFF',
                    'target-arrow-shape': 'triangle',
                    'curve-style': 'bezier'
                }
            },
            {
                selector: '.node-disabled',
                style: {
                    'background-color': '#B0BEC5',
                    'color': '#000000',
                    'text-opacity': 0.75,
                    'border-style': 'dashed'
                }
            }
        ];
    }

    getLayoutConfig() {
        return {
            name: 'dagre',
            directed: true,
            padding: 10,
            rankDir: 'TB',
            nodeSep: 50,
            rankSep: 75,
            edgeSep: 10
        };
    }

    attachEventListeners() {
        this.cy.on('tap', 'node', (evt) => {
            const node = evt.target;
            const nodeId = node.id();

            if (this.disabledNodes.has(nodeId)) {
                this.enableNodeAndDependents(node);
            } else {
                this.disableNodeAndDependents(node);
            }

            this.sendDisabledNodesToJava();
        });
    }

    disableNodeAndDependents(node) {
        const nodeId = node.id();
        if (this.disabledNodes.has(nodeId)) return;

        this.disabledNodes.add(nodeId);
        node.addClass('node-disabled');

        node.outgoers('node').forEach((childNode) => {
            const hasActiveDependency = childNode.incomers('edge')
                .some(edge => !this.disabledNodes.has(edge.source().id()));

            if (!hasActiveDependency) {
                this.disableNodeAndDependents(childNode);
            }
        });
    }

    enableNodeAndDependents(node) {
        const nodeId = node.id();
        if (!this.disabledNodes.has(nodeId)) return;

        this.disabledNodes.delete(nodeId);
        node.removeClass('node-disabled');

        node.outgoers('node').forEach((childNode) => {
            const hasActiveDependency = childNode.incomers('edge')
                .some(edge => !this.disabledNodes.has(edge.source().id()));

            if (hasActiveDependency) {
                this.enableNodeAndDependents(childNode);
            }
        });
    }

    sendDisabledNodesToJava() {
        if (window.sendToJava) {
            window.sendToJava(JSON.stringify([...this.disabledNodes]));
        }
    }

    setGraphData(jsonString) {
        const data = JSON.parse(jsonString);
        this.initializeGraph(data);
    }

    setGraphDataWhenReady(jsonString) {
        if (this.isReady) {
            this.setGraphData(jsonString);
        } else {
            setTimeout(() => this.setGraphDataWhenReady(jsonString), 500);
        }
    }
}

// ✅ Initialize the GraphManager instance
const graphManager = new GraphManager('cy');

// ✅ Global functions to be used by external JavaScript or Java integration
function setGraphData(jsonString) {
    graphManager.setGraphData(jsonString);
}

function setGraphDataWhenReady(jsonString) {
    graphManager.setGraphDataWhenReady(jsonString);
}
